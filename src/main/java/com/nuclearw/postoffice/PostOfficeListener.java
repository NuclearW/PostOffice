package com.nuclearw.postoffice;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();

		if(block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			if(isPostOfficeSign(sign)) {
				Player player = event.getPlayer();

				if(!player.hasPermission("postoffice.break")) {
					player.sendMessage("You do not have permission to do that");
					event.setCancelled(true);
				}
			}
		} else if(block.getType().equals(Material.WOOL)) {
			// TODO: Direct breaking of blocks
		}
	}

	private boolean isPostOfficeSign(Sign sign) {
		String text = sign.getLine(0);
		if(text == null) return false;

		if(text.equals("[POBox]") || text.equals("[Mailbox]")) return true;

		return false;
	}
}
