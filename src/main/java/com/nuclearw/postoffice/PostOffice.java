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

		// Database
		initDatabase();

		// Hello, world
		getLogger().info("Finished Loading " + getDescription().getFullName());
	}

	@Override
	public void onDisable() {
		getLogger().info("Finished Unloading "+getDescription().getFullName());
	}

	@Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(POBox.class);
        return list;
    }

	private void initDatabase() {
        try {
            getDatabase().find(POBox.class).findRowCount();
        } catch (PersistenceException ex) {
            getLogger().info("Initializing database");
            this.installDDL();
        }
	}
}
