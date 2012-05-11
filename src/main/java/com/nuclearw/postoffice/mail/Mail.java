package com.nuclearw.postoffice.mail;

import java.io.Serializable;

public interface Mail extends Serializable {
	/**
	 * Find who this mail was sent to
	 * 
	 * @return String name of the Player to whom this mail was sent
	 */
	public String sentTo();

	/**
	 * Find who sent this mail
	 * 
	 * @return String name of the Player who sent this mail
	 */
	public String sentFrom();

	/**
	 * Find when the mail was sent
	 *
	 * @return The time the the mail was sent
	 */
	public long sentAt();
}
