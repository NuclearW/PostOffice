package com.nuclearw.postoffice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.nuclearw.postoffice.mail.Letter;
import com.nuclearw.postoffice.mail.Mail;
import com.nuclearw.postoffice.mail.Package;

public class PostMaster {
	private static PostMaster instance;
	private static PostOffice plugin;

	private static File dataDir;

	private PostMaster(PostOffice plugin) {
		PostMaster.plugin = plugin;

		File rootDir = plugin.getDataFolder();

		if(!rootDir.exists())
			rootDir.mkdir();

		PostMaster.dataDir = new File(rootDir, "boxes");

		if(!dataDir.exists())
			dataDir.mkdir();

		File allUsers = new File(dataDir, "__all_users__");
		if(!allUsers.exists())
			allUsers.mkdir();
	}

	/**
	 * Get or create the instance of PostMaster
	 * 
	 * @param plugin The PostOffice plugin instance
	 * @return The PostMaster instance
	 */
	protected static PostMaster getInstance(PostOffice plugin) {
		if(instance != null)
			return instance;
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
		if(mail.sentTo().equals("__ALL_USERS__")) {
			for(String entry : dataDir.list()) {
				if(entry.equals("__ALL_USERS__"))
					continue;

				File box = new File(dataDir, entry);
				if(!box.isDirectory())
					continue;

				int mailIndex = getBoxSize(box);

				File location = new File(box, "" + mailIndex);

				serializeMail(mail, location);
			}
			return;
		}
		File box = getBox(mail.sentTo());

		if(!hasBox(box)) {
			makeBox(box);
		}

		int mailIndex = getBoxSize(box);

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

			if(from == null)
				return false; // Fail on null player

			ItemStack item = pack.getItem();
			Inventory inventory = from.getInventory();

			HashMap<Integer, ItemStack> extra = inventory.addItem(item);

			if(!extra.isEmpty()) {
				for(ItemStack extras : extra.values()) {
					Package extraPack = new Package(fromName, "CONSOLE", extras);
					sendMail(extraPack);
				}
				from.sendMessage("Oops! Some items didn't fit so I re-sent them to you!");
			}

			return status;
		} else {
			// Cannot return letters
			return true;
		}
	}

	/**
	 * Deliver mail to a person
	 * 
	 * @param mail Mail to deliver
	 * @return True if the mail was delivered, false if there was an error
	 */
	@SuppressWarnings ("deprecation")
	public static boolean deliverMail(Mail mail) {
		boolean status = true;

		String toName = mail.sentTo();
		Player to = plugin.getServer().getPlayer(toName);

		String fromName = mail.sentFrom();

		Date sentDate = new Date(mail.sentAt());
		String sentAt = DateFormat.getDateTimeInstance().format(sentDate);

		if(to == null)
			return false; // Fail on null player

		if(mail instanceof Letter) {
			Letter letter = (Letter) mail;

			String messageBody = letter.getMessage();

			to.sendMessage("A letter from: " + fromName);
			to.sendMessage("Written at: " + sentAt);
			to.sendMessage("Message: " + messageBody);
		} else {
			Package pack = (Package) mail;

			ItemStack item = pack.getItem();
			Inventory inventory = to.getInventory();

			if(inventory.firstEmpty() == -1) {
				to.sendMessage("Not enough room to open this package!");
				status = false;
			} else {
				HashMap<Integer, ItemStack> extra = inventory.addItem(item);

				if(!extra.isEmpty()) {
					// Send extras back to user
					for(Integer i : extra.keySet()) {
						ItemStack stack = extra.get(i);
						Package extrasPack = new Package(to.getName(), Bukkit.getConsoleSender().getName(), stack); // Send as console
						sendMail(extrasPack);
					}
					to.sendMessage("Whoops! Your inventory was full. Your missing items are re-sent to you.");
				} else {
					to.sendMessage("A package from: " + fromName);
					to.sendMessage("Packaged at: " + sentAt);
					to.sendMessage(item.getAmount() + "x " + materialToHuman(item.getType()));
				}
				to.updateInventory();
			}
		}
		return status;
	}

	/**
	 * Converts a Material to a human-readable String<br>
	 * Example: Material.DIAMOND_BLOCK returns "Diamond Block"
	 * 
	 * @param material the material
	 * @return the "human" string
	 */
	public static String materialToHuman(Material material) {
		String name = material.name();
		StringBuilder newName = new StringBuilder();
		for(String part : name.split("_")) {
			part = part.toLowerCase();
			String fletter = part.substring(0, 1);
			String rest = part.substring(1);
			fletter = fletter.toUpperCase();
			newName.append(fletter).append(rest).append(" ");
		}
		return newName.toString().trim();
	}

	protected static File getBox(String name) {
		return new File(dataDir, name.toLowerCase());
	}

	protected static boolean hasBox(File box) {
		return box.isDirectory();
	}

	protected static boolean makeBox(File box) {
		return box.mkdir();
	}

	protected static boolean deleteBox(File box) {
		boolean status = true;

		if(box.list() == null) {
			return true; // We didn't have anything to delete
		}

		for(String item : box.list()) {
			File delete = new File(box, item);
			if(!delete.delete())
				status = false;
		}

		if(!box.delete())
			status = false;

		return status;
	}

	private static List<Mail> getMail(String name, boolean empty) {
		List<Mail> mail = new ArrayList<Mail>();

		File box = getBox(name);
		if(!hasBox(box)) {
			return mail; // Does not have box?  Return empty list
		}

		if(!isBoxEmpty(box)) {
			int length = getBoxSize(box);
			for(int i = 0; i < length; i++) {
				File location = new File(box, "" + i);
				mail.add(deserializeMail(location));
			}
		}

		if(empty) {
			emptyBox(box);
		}

		return mail;
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
			if(test == false)
				result = false;
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
		} catch(IOException ex) {
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
		} catch(IOException ex) {
			ex.printStackTrace();
		} catch(ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return mail;
	}
}
