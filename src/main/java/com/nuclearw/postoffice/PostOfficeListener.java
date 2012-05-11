package com.nuclearw.postoffice;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Wool;

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

				/*
				File box = PostMaster.getBox(sign.getLine(1));
				PostMaster.deleteBox(box);
				*/
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

				/*
				File box = PostMaster.getBox(found.getLine(1));
				PostMaster.deleteBox(box);
				*/
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
