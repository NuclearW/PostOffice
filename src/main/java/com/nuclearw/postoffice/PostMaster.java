package com.nuclearw.postoffice;

import java.io.File;
import java.util.List;

import com.nuclearw.postoffice.mail.Mail;

public class PostMaster {
	private static PostMaster instance;
	private static PostOffice plugin;

	private static File dataDir;

	private PostMaster(PostOffice plugin) {
		this.plugin = plugin;

		File rootDir = plugin.getDataFolder();
		this.dataDir = new File(rootDir, "boxes");
	}

	/**
	 * Get or create the instance of PostMaster
	 *
	 * @param plugin The PostOffice plugin instance
	 * @return The PostMaster instance
	 */
	protected static PostMaster getInstance(PostOffice plugin) {
		if(instance != null) return instance;
		instance = new PostMaster(plugin);
		return instance;
	}

	/**
	 * Get the instance of PostMaster
	 *
	 * @return The PostMaster instance, or null if it has not been made yet
	 */
	public static PostMaster getInstance() {
		return instance;
	}

	/**
	 * Send mail
	 *
	 * @param mail Mail to be sent
	 */
	public static void sendMail(Mail mail) {
		// TODO: Handle mail
	}

	/**
	 * Return all mail from a box and empty it
	 *
	 * @param name Name of the player's mail to retrieve
	 * @return A List of all Mail objects for that user
	 */
	public static List<Mail> getMail(String name) {
		return getMail(name, true);
	}

	/**
	 * Return all mail from a box but do not empty it
	 *
	 * @param name Name of the player's mail to retrieve
	 * @return A List of all Mail objects for that user
	 */
	public static List<Mail> readMail(String name) {
		return getMail(name, false);
	}

	private static List<Mail> getMail(String name, boolean empty) {
		// TODO: Get mail
		if(empty) {
			// TODO: Empty mailbox
		}
		return null;
	}
}
