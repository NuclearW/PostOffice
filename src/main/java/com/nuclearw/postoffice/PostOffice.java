package com.nuclearw.postoffice;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.nuclearw.postoffice.mail.Mail;

public class PostOffice extends JavaPlugin {
	private static PostMaster manager;

	private static Map<Player, Mail> held = new HashMap<Player, Mail>();

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
		// Attempt to return all held packages
		for(Mail mail : held.values()) {
			PostMaster.returnMail(mail);
		}

		// Goodbye, world
		getLogger().info("Finished Unloading "+getDescription().getFullName());
	}

	/**
	 * Get if a player is currently holding mail
	 *
	 * @param player Player to check if holding mail
	 * @return True if the player is holding mail currently, false if not
	 */
	public boolean isHoldingMail(Player player) {
		return held.containsKey(player);
	}

	/**
	 * Get held mail
	 *
	 * @param player Player to get held mail from
	 * @return The mail the player is holding
	 */
	public Mail getHeldMail(Player player) {
		return held.get(player);
	}

	/**
	 * Give mail to a player to hold
	 *
	 * @param player Player to give mail to
	 * @param mail Mail to hold
	 * @return True if mail was given, false if not
	 */
	public boolean holdMail(Player player, Mail mail) {
		if(isHoldingMail(player)) return false;
		return (held.put(player, mail) != null);
	}

	/**
	 * Remove held mail from a player
	 *
	 * @param player Player to remove mail from
	 * @return True if mail was removed, false if not
	 */
	public boolean removeHeldMail(Player player) {
		if(!isHoldingMail(player)) return false;

		if(held.remove(player) == null) return false;
		return true;
	}
}
