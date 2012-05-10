package com.nuclearw.postoffice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name="po_mailbox")
public class Mailbox {
	@Id
	@Column(name="id")
	private int id;

	@NotEmpty
	@NotNull
	@Column(name="world")
	private String world;

	@NotEmpty
	@NotNull
	@Column(name="id")
	private int x;

	@NotEmpty
	@NotNull
	@Column(name="id")
	private int y;

	@NotEmpty
	@NotNull
	@Column(name="id")
	private int z;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
}
