package com.nuclearw.postoffice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.nuclearw.postoffice.mail.Mail;
import com.nuclearw.postoffice.mail.Package;

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
		File box = getBox(mail.sentTo());

		if(!hasBox(box)) {
			makeBox(box);
		}

		int mailIndex = getBoxSize(box) + 1;

		File location = new File(box, "" + mailIndex);

		serializeMail(mail, location);
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

	/**
	 * Return mail to a person
	 *
	 * @param mail Mail to return
	 * @return True if the mail could be returned, false if there was an error
	 */
	public static boolean returnMail(Mail mail) {
		if(mail instanceof Package) {
			boolean status = true;

			Package pack = (Package) mail;

			String fromName = pack.sentFrom();
			Player from = plugin.getServer().getPlayer(fromName);

			ItemStack item = pack.getItem();
			Inventory inventory = from.getInventory();

			HashMap<Integer, ItemStack> extra = inventory.addItem(item);

			if(extra.isEmpty()) {
				return true;
			} else {
				for(ItemStack extras : extra.values()) {
					Item dropped = from.getWorld().dropItemNaturally(from.getLocation(), extras);
					if(dropped == null) status = false;
				}
			}

			return status;
		} else {
			// Cannot return letters
			return true;
		}
	}

	private static List<Mail> getMail(String name, boolean empty) {
		List<Mail> mail = new ArrayList<Mail>();

		File box = getBox(name);
		if(!hasBox(box)) {
			return mail;	// Does not have box?  Return empty list
		}

		if(!isBoxEmpty(box)) {
			int length = getBoxSize(box);
			for(int i = 0; i <= length; i++) {
				File location = new File(box, "" + i);
				mail.add(deserializeMail(location));
			}
		}

		if(empty) {
			emptyBox(box);
		}

		return mail;
	}

	private static File getBox(String name) {
		return new File(dataDir, name);
	}

	private static boolean hasBox(File box) {
		return box.isDirectory();
	}

	private static boolean makeBox(File box) {
		return box.mkdir();
	}

	private static boolean isBoxEmpty(File box) {
		return box.list().length == 0;
	}

	private static int getBoxSize(File box) {
		return box.list().length;
	}

	private static boolean emptyBox(File box) {
		boolean result = true;

		for(String file : box.list()) {
			File mail = new File(box, file);
			boolean test = mail.delete();
			if(test == false) result = false;
		}

		return result;
	}

	private static void serializeMail(Mail mail, File location) {
		try {
			FileOutputStream fileOut = new FileOutputStream(location);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(mail);
			objOut.close();
			fileOut.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static Mail deserializeMail(File location) {
		Mail mail = null;

		try {
			FileInputStream fileIn = new FileInputStream(location);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			mail = (Mail) objIn.readObject();
			objIn.close();
			fileIn.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return mail;
	}
}
