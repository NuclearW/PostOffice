package com.nuclearw.postoffice;

import java.io.File;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.nuclearw.postoffice.mail.Mail;

public class PostOfficeListener implements Listener {
	private static PostOffice plugin;

	public PostOfficeListener(PostOffice plugin) {
		PostOfficeListener.plugin = plugin;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if(plugin.isHoldingMail(player)) {
			PostMaster.returnMail(plugin.getHeldMail(player));
			plugin.removeHeldMail(player);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if(block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			if(isPostOfficeSign(sign)) {

				if(!player.hasPermission("postoffice.break")) {
					player.sendMessage("You do not have permission to do that");
					event.setCancelled(true);
					return;
				}

				if(sign.getLine(0).equalsIgnoreCase("[POBox]")) {
					File box = PostMaster.getBox(sign.getLine(1));
					if(box != null) {
						boolean gone = PostMaster.deleteBox(box);
						if(!gone) {
							player.sendMessage("Oops! POBox not deleted!");
						} else {
							player.sendMessage("POBox deleted.");

							// Delete the bottom block too
							if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.ADVENTURE) {
								block.getRelative(BlockFace.DOWN).setType(Material.AIR);
							} else {
								block.getRelative(BlockFace.DOWN).breakNaturally();
							}
						}
					}
				} else if(sign.getLine(0).equalsIgnoreCase("[Mailbox]")) {
					player.sendMessage("Mailbox deleted.");
					// Delete the bottom block too
					if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.ADVENTURE) {
						block.getRelative(BlockFace.DOWN).setType(Material.AIR);
					} else {
						block.getRelative(BlockFace.DOWN).breakNaturally();
					}
				}
			}
		} else if(block.getType().equals(Material.WOOL)) {
			Sign found = null;

			Block above = block.getRelative(BlockFace.UP);
			if(above.getState() instanceof Sign) {
				Sign potential = (Sign) above.getState();
				if(isPostOfficeSign(potential)) {
					found = potential;
				}
			}

			if(found != null && !player.hasPermission("postoffice.break")) {
				player.sendMessage("You do not have permission to do that");
				event.setCancelled(true);
				return;
			}
			if(found != null) {
				boolean del = false;
				if(found.getLine(0).equalsIgnoreCase("[POBox]")) {
					File box = PostMaster.getBox(found.getLine(1));
					if(box != null) {
						boolean gone = PostMaster.deleteBox(box);
						if(!gone) {
							player.sendMessage("Oops! POBox not deleted!");
						} else {
							player.sendMessage("POBox deleted.");
							del = true;
						}
					}
				} else if(found.getLine(0).equalsIgnoreCase("[Mailbox]")) {
					del = true;
					player.sendMessage("Mailbox deleted.");
				}
				if(del) {
					// Delete the sign too
					if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.ADVENTURE) {
						found.getBlock().setType(Material.AIR);
					} else {
						found.getBlock().breakNaturally();
					}
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		String marker = event.getLine(0);

		// Fail out if we don't have anything to read
		if(marker == null || marker.trim().length() == 0) {
			return;
		}

		Player player = event.getPlayer();
		if(marker.equalsIgnoreCase("[POBox]")) {
			if(!player.hasPermission("postoffice.make")) {
				player.sendMessage("You do not have permission to do that");
				event.setLine(0, "");
				return;
			}

			if(marker.equalsIgnoreCase("[POBox]")) {
				event.setLine(0, "[POBox]");

				String targetName = event.getLine(1);
				if(targetName == null || targetName.trim().length() == 0) {
					player.sendMessage("You didn't define a player on line 2 of the sign!");
					event.setLine(0, "");
					return;
				}

				// Attempt to match based on online users
				Player to = plugin.getServer().getPlayer(targetName);
				if(to != null) {
					// If we have a match, set it
					targetName = to.getName();
				} else {
					// If not, attempt to match based on offline users
					OfflinePlayer toOffline = plugin.getServer().getOfflinePlayer(targetName);
					if(toOffline != null) {
						// If we have a match, set it
						targetName = toOffline.getName();
					}
				}

				event.setLine(1, targetName);

				Block below = event.getBlock().getRelative(BlockFace.DOWN);
				if(below.getType() != Material.AIR && below.getType() != Material.WOOL) {
					player.sendMessage("No space for a wool block below the sign! Clear a space or place one please!");
					event.setLine(0, "ERROR");
					event.setLine(1, "NO WOOL");
					event.setLine(2, "BELOW SIGN");
					return;
				}

				File box = PostMaster.getBox(targetName);
				if(!PostMaster.hasBox(box)) {
					boolean made = PostMaster.makeBox(box);
					if(!made) {
						player.sendMessage("Oops! POBox failed for unknown reason.");
					} else {
						player.sendMessage("POBox created!");
						below.setType(Material.WOOL);
					}
				} else {
					player.sendMessage("POBox already exists!");
				}
			}
		} else if(marker.equalsIgnoreCase("[Mailbox]")) {
			event.setLine(0, "[Mailbox]");
			Block below = event.getBlock().getRelative(BlockFace.DOWN);
			if(below.getType() != Material.AIR && below.getType() != Material.WOOL) {
				player.sendMessage("No space for a wool block below the sign! Clear a space or place one please!");
				event.setLine(0, "ERROR");
				event.setLine(1, "NO WOOL");
				event.setLine(2, "BELOW SIGN");
				return;
			}
			player.sendMessage("Mailbox created");
			below.setType(Material.WOOL);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if(block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			if(isPostOfficeSign(sign)) {
				processInteraction(event, sign, player);
			}
		} else if(block.getType().equals(Material.WOOL)) {
			if(block.getData() == 0x0 || block.getData() == 0xB) {
				Sign found = null;

				Block above = block.getRelative(BlockFace.UP);
				if(above.getState() instanceof Sign) {
					Sign potential = (Sign) above.getState();
					if(isPostOfficeSign(potential)) {
						found = potential;
					}
				}

				if(found != null) {
					processInteraction(event, found, player);
				}
			}
		}
	}

	private void processInteraction(PlayerInteractEvent event, Sign sign, Player player) {
		if(sign.getLine(0).equalsIgnoreCase("[Mailbox]")) {
			if(plugin.isHoldingMail(player)) {
				PostMaster.sendMail(plugin.getHeldMail(player));
				plugin.removeHeldMail(player);
				player.sendMessage("Mail sent");
			} else {
				player.sendMessage("You aren't holding any mail");
			}
		} else {
			String name = sign.getLine(1);

			if(!name.equals(player.getName())) {
				player.sendMessage("This isn't your mailbox");
			} else {
				List<Mail> mails = PostMaster.getMail(name);
				if(mails == null || mails.isEmpty()) {
					player.sendMessage("No mail");
				} else {
					for(Mail mail : mails) {
						if(!PostMaster.deliverMail(mail)) {
							player.sendMessage("Couldn't open that mail, putting it back in the box");
							PostMaster.sendMail(mail);
						}
					}
				}
			}
		}
		event.setCancelled(true);
	}

	private boolean isPostOfficeSign(Sign sign) {
		String text = sign.getLine(0);
		if(text == null)
			return false;

		boolean state = false;

		if(text.equals("[POBox]") || text.equals("[Mailbox]"))
			state = true;

		if(state)
			state = sign.getBlock().getRelative(BlockFace.DOWN).getType() == Material.WOOL;

		return state;
	}

}
