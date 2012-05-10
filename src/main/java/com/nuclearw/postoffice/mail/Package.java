package com.nuclearw.postoffice.mail;

import org.bukkit.inventory.ItemStack;

public class Package implements Mail {
	private static final long serialVersionUID = 8606293052193623144L;

	private String sentTo, sentFrom;
	private ItemStack item;

	public Package(String sentTo, String sentFrom, ItemStack item) {
		this.sentTo = sentTo;
		this.sentFrom = sentFrom;
		this.item = item;
	}

	public ItemStack getItem() {
		return item;
	}

	@Override
	public String sentTo() {
		return sentTo;
	}

	@Override
	public String sentFrom() {
		return sentFrom;
	}
}
