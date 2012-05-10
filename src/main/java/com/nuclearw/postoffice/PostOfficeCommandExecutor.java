package com.nuclearw.postoffice;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			// TODO: Make mail to be sent
		}

		return true;
	}
}
