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
		if(!label.equalsIgnoreCase("mail")) return false;

		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("cancel")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage("Console cannot cancel mail.");
					return true;
				}

				Player player = (Player) sender;

				if(plugin.isHoldingMail(player)) {
					if(PostMaster.returnMail(plugin.getHeldMail(player))) {
						player.sendMessage("Mail returned.");
					} else {
						player.sendMessage("Mail could not be returned, mail lost.");
					}
					plugin.removeHeldMail(player);
				} else {
					player.sendMessage("You aren't holding mail!");
				}
			} else {
				return false;
			}
		} else if(args.length > 2) {
			String toName = args[0];

			Player to = plugin.getServer().getPlayer(toName);
			if(to != null) {
				toName = to.getName();
			} else {
				OfflinePlayer toOffline = plugin.getServer().getOfflinePlayer(toName);
				if(toOffline != null) {
					toName = toOffline.getName();
				}
			}

			if(toName.equals("*")) {
				if(!sender.hasPermission("postoffice.mail.all")) {
					sender.sendMessage("You do not have permission to do that");
					return true;
				}
				toName = "__ALL_USERS__";
			}

			File box = PostMaster.getBox(toName);
			if(!PostMaster.hasBox(box)) {
				sender.sendMessage(toName + " does not have a box.  Mail cannot be sent.");
				return true;
			} else {
				if(args[1].equalsIgnoreCase("package")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage("Console cannot send packages.");
						return true;
					}

					Player from = (Player) sender;

					if(!from.hasPermission("postoffice.package")) {
						from.sendMessage("You do not have permission to do that");
						return true;
					}

					if(plugin.isHoldingMail(from)) {
						from.sendMessage("You are already holding mail.");
						return true;
					}

					ItemStack item = from.getItemInHand();
					if(item == null || item.getAmount() == 0 || item.getTypeId() == 0) {
						from.sendMessage("You can't send nothing.");
						return true;
					}

					String fromName = from.getName();

					Package pack = new Package(toName, fromName, item);
					if(!plugin.holdMail(from, pack)) {
						from.sendMessage("Error making mail!");
						return true;
					}

					from.sendMessage("You have prepared a package for: " + toName);
					from.sendMessage(item.getAmount() + "x " + item.getType().toString());

					from.setItemInHand(new ItemStack(0));
				} else {
					// TODO: Letter
				}
			}
		}

		return true;
	}
}
