package com.virnect.account.domain.enumclass;

import java.security.SecureRandom;

import lombok.Getter;

public enum Color {
	GREEN("https://image.squars.io/group/green.png"),
	TEAL("https://image.squars.io/group/teal.png"),
	BLUE("https://image.squars.io/group/blue.png"),
	VIOLET("https://image.squars.io/group/violet.png"),
	PURPLE("https://image.squars.io/group/purple.png"),
	PINK("https://image.squars.io/group/pink.png");

	@Getter
	private final String colorUrl;

	Color(String colorUrl) {
		this.colorUrl = colorUrl;
	}

	private static final int size = values().length;
	private static final SecureRandom random = new SecureRandom();

	public static Color getRandomColor() {
		return values()[(random.nextInt(size))];
	}
}
