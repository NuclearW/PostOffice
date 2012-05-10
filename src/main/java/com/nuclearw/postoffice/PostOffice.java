package com.nuclearw.postoffice;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bukkit.plugin.java.JavaPlugin;

public class PostOffice extends JavaPlugin {
	private static PostMaster manager;

	@Override
	public void onEnable() {
		// Make our manager
		manager = PostMaster.getInstance(this);

		// Register listeners
		getServer().getPluginManager().registerEvents(new PostOfficeListener(this), this);

		// Register command
		getCommand("mail").setExecutor(new PostOfficeCommandExecutor(this));

		// Hello, world
		getLogger().info("Finished Loading " + getDescription().getFullName());
	}

	@Override
	public void onDisable() {
		getLogger().info("Finished Unloading "+getDescription().getFullName());
	}
}
