package com.nuclearw.postoffice.mail;

import org.bukkit.inventory.ItemStack;

import com.nuclearw.postoffice.util.CardboardBox;

public class Package implements Mail {
	private static final long serialVersionUID = 8606293052193623144L;

	private final String sentTo, sentFrom;
	private final CardboardBox item;
	private final long sentAt;

	public Package(String sentTo, String sentFrom, ItemStack item) {
		this.sentTo = sentTo;
		this.sentFrom = sentFrom;
		this.item = new CardboardBox(item);
		this.sentAt = System.currentTimeMillis();
	}

	public ItemStack getItem() {
		return item.unbox();
	}

	@Override
	public String sentTo() {
		return sentTo;
	}

	@Override
	public String sentFrom() {
		return sentFrom;
	}

	@Override
	public long sentAt() {
		return sentAt;
	}
}
