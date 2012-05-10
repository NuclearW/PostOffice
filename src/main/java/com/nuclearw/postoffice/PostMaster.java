package com.nuclearw.postoffice;

import org.bukkit.Location;

public class PostMaster {
	private static PostMaster instance;
	private static PostOffice plugin;

	private PostMaster(PostOffice plugin) {
		this.plugin = plugin;
	}

	public static PostMaster getInstance(PostOffice plugin) {
		if(instance != null) return instance;
		instance = new PostMaster(plugin);
		return instance;
	}
}
