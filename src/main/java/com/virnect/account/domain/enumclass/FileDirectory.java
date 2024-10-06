package com.virnect.account.domain.enumclass;

public enum FileDirectory {
	EVENT_POPUP("service/event-popup-media"),
	EMAIL_CUSTOMIZING_MANAGEMENT("service/email-customizing-media"),
	USER_PROFILE("users/%s/profiles");

	private final String directory;

	FileDirectory(String directory) {
		this.directory = directory;
	}

	public String getDirectory(Long id) {
		if (this == USER_PROFILE) {
			return String.format(this.directory, id);
		}
		return directory;
	}
}
