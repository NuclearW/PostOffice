package com.nuclearw.postoffice.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * A serializable ItemStack
 */
public class CardboardBox implements Serializable {
	private static final long serialVersionUID = 729890133797629668L;

	private final int type, amount;
	private final short damage;
	private final byte data;

	private final HashMap<CardboardEnchantment, Integer> enchants;

	public CardboardBox(ItemStack item) {
		this.type = item.getTypeId();
		this.amount = item.getAmount();
		this.damage = item.getDurability();
		this.data = item.getData().getData();

		HashMap<CardboardEnchantment, Integer> map = new HashMap<CardboardEnchantment, Integer>();

		Map<Enchantment, Integer> enchantments = item.getEnchantments();

		for(Enchantment enchantment : enchantments.keySet()) {
			map.put(new CardboardEnchantment(enchantment), enchantments.get(enchantment));
		}

		enchants = map;
	}

	public ItemStack unbox() {
		ItemStack item = new ItemStack(type, amount, damage, data);

		HashMap<Enchantment, Integer> map = new HashMap<Enchantment, Integer>();

		for(CardboardEnchantment cEnchantment : enchants.keySet()) {
			map.put(cEnchantment.unbox(), enchants.get(cEnchantment));
		}

		item.addUnsafeEnchantments(map);

		return item;
	}
}
