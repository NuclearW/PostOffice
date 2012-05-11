package com.nuclearw.postoffice;

import java.io.File;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Wool;

import com.nuclearw.postoffice.mail.Mail;

public class PostOfficeListener implements Listener {
	private static PostOffice plugin;

	public PostOfficeListener(PostOffice plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if(plugin.isHoldingMail(player)) {
			PostMaster.returnMail(plugin.getHeldMail(player));
			plugin.removeHeldMail(player);
		}
	}

	@EventHandler
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

				/**/
				File box = PostMaster.getBox(sign.getLine(1));
				PostMaster.deleteBox(box);
				/**/
			}
		} else if(block instanceof Wool) {
			Wool wool = (Wool) block;
			if(wool.getColor().equals(DyeColor.BLUE) || wool.getColor().equals(DyeColor.WHITE)) {
				Sign found = null;

				Block[] blocks = getSurroundingBlocks(block);
				for(Block b : blocks) {
					if(b != null && isPostOfficeSign(b)) {
						found = (Sign) b.getState();
						break;
					}
				}

				if(found != null && !player.hasPermission("postoffice.break")) {
					player.sendMessage("You do not have permission to do that");
					event.setCancelled(true);
					return;
				}

				/**/
				File box = PostMaster.getBox(found.getLine(1));
				PostMaster.deleteBox(box);
				/**/
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		String marker = event.getLine(0);

		if(marker.equalsIgnoreCase("[POBox]") || marker.equalsIgnoreCase("[Mailbox]")) {
			Player player = event.getPlayer();

			if(!player.hasPermission("postoffice.make")) {
				player.sendMessage("You do not have permission to do that");
				event.setLine(0, "");
				return;
			}

			if(marker.equalsIgnoreCase("[POBox]")) {
				event.setLine(0, "[POBox]");

				String targetName = event.getLine(1);

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

				File box = PostMaster.getBox(targetName);
				if(!PostMaster.hasBox(box)) {
					PostMaster.makeBox(box);
				}
			}
			else event.setLine(0, "[Mailbox]");
		}
	}

	@EventHandler
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
		} else if(block instanceof Wool) {
			Wool wool = (Wool) block;
			if(wool.getColor().equals(DyeColor.BLUE) || wool.getColor().equals(DyeColor.WHITE)) {
				Sign found = null;

				Block[] blocks = getSurroundingBlocks(block);
				for(Block b : blocks) {
					if(b != null && isPostOfficeSign(b)) {
						found = (Sign) b.getState();
						break;
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
	}

	private boolean isPostOfficeSign(Block block) {
		if(block.getState() instanceof Sign) {
			return isPostOfficeSign((Sign) block.getState());
		}
		return false;
	}

	private boolean isPostOfficeSign(Sign sign) {
		String text = sign.getLine(0);
		if(text == null) return false;

		if(text.equals("[POBox]") || text.equals("[Mailbox]")) return true;

		return false;
	}

	private Block[] getSurroundingBlocks(Block block) {
		Block[] blocks = new Block[6];

		blocks[0] = block.getRelative(BlockFace.UP);

		return blocks;
	}
}
