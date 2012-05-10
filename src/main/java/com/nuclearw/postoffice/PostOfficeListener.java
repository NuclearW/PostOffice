package com.nuclearw.postoffice;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
}
