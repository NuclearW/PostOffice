package com.nuclearw.postoffice.mail;

public class Letter implements Mail {
	private static final long serialVersionUID = 1474828356587940254L;

	private final String sentTo, sentFrom, message;
	private final long sentAt;

	public Letter(String sentTo, String sentFrom, String message) {
		this.sentTo = sentTo;
		this.sentFrom = sentFrom;
		this.message = message;
		this.sentAt = System.currentTimeMillis();
	}

	public String getMessage() {
		return message;
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
