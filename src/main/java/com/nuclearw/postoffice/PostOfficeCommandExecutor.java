package com.nuclearw.postoffice;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PostOfficeCommandExecutor implements CommandExecutor {
	private static PostOffice plugin;

	public PostOfficeCommandExecutor(PostOffice plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

}