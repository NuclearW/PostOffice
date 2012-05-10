package com.nuclearw.postoffice;

import java.io.File;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nuclearw.postoffice.mail.Letter;
import com.nuclearw.postoffice.mail.Mail;
import com.nuclearw.postoffice.mail.Package;

public class PostOfficeCommandExecutor implements CommandExecutor {
	private static PostOffice plugin;

	public PostOfficeCommandExecutor(PostOffice plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// If not mail command, bail
		if(!label.equalsIgnoreCase("mail")) return false;

		// If one arg, cancel or bust
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("cancel")) {
				// Bad console
				if(!(sender instanceof Player)) {
					sender.sendMessage("Console cannot cancel mail.");
					return true;
				}

				// Get our player
				Player player = (Player) sender;

				// Check to see if we are returning something
				if(plugin.isHoldingMail(player)) {
					// Will return false on letters, but otherwise will return items
					if(PostMaster.returnMail(plugin.getHeldMail(player))) {
						player.sendMessage("Mail returned.");
					} else {
						player.sendMessage("Mail could not be returned, mail lost.");
					}
					plugin.removeHeldMail(player);
				} else {
					// Can't return nothin'
					player.sendMessage("You aren't holding mail!");
				}
			} else {
				// Invalid args
				return false;
			}
		} else if(args.length > 2) {
			// Args > 2 we are actually sending things now
			// First arg in this case will be our target to send to
			String toName = args[0];

			// Attempt to match based on online users
			Player to = plugin.getServer().getPlayer(toName);
			if(to != null) {
				// If we have a match, set it
				toName = to.getName();
			} else {
				// If not, attempt to match based on offline users
				OfflinePlayer toOffline = plugin.getServer().getOfflinePlayer(toName);
				if(toOffline != null) {
					// If we have a match, set it
					toName = toOffline.getName();
				}
			}

			// Special case name for "all users"
			if(toName.equals("*")) {
				// Need a permission for this
				if(!sender.hasPermission("postoffice.mail.all")) {
					sender.sendMessage("You do not have permission to do that");
					return true;
				}
				// Set name to this magical deal, guaranteed to have a box
				toName = "__ALL_USERS__";
			}

			// Get box and check if exists
			File box = PostMaster.getBox(toName);
			if(!PostMaster.hasBox(box)) {
				// Can't send to someone without a box
				sender.sendMessage(toName + " does not have a box.  Mail cannot be sent.");
				return true;
			} else {
				// Check for package, if not then letter
				if(args[1].equalsIgnoreCase("package")) {
					// Bad console
					if(!(sender instanceof Player)) {
						sender.sendMessage("Console cannot send packages.");
						return true;
					}

					// Get our sender
					Player from = (Player) sender;

					// Check permissions
					if(!from.hasPermission("postoffice.package")) {
						from.sendMessage("You do not have permission to do that");
						return true;
					}

					// Check to see if already holding mail
					if(plugin.isHoldingMail(from)) {
						from.sendMessage("You are already holding mail.");
						return true;
					}

					// See what is in hand, can't send nothing.
					ItemStack item = from.getItemInHand();
					if(item == null || item.getAmount() == 0 || item.getTypeId() == 0) {
						from.sendMessage("You can't send nothing.");
						return true;
					}

					// Grab name of our sender
					String fromName = from.getName();

					// Create package and give it to user to hold
					Package pack = new Package(toName, fromName, item);
					if(!plugin.holdMail(from, pack)) {
						// Something went wrong, this should not happen
						from.sendMessage("Error making mail!");
						plugin.getLogger().severe("Error making mail for " + fromName + "!");
						return true;
					}

					// Notify user of mail they are holding
					from.sendMessage("You have prepared a package for: " + toName);
					from.sendMessage(item.getAmount() + "x " + item.getType().toString());

					// Remove item from their hand
					from.setItemInHand(new ItemStack(0));
				} else {
					// TODO: Letter
				}
			}
		}

		return true;
	}
}
