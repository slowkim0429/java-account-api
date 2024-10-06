package com.virnect.account.domain.enumclass;

public enum FileType {
	IMAGE, VIDEO;

	public boolean isVideo() {
		return this.equals(VIDEO);
	}

	public boolean isImage() {
		return this.equals(IMAGE);
	}
}
