package com.virnect.account.domain.enumclass;

public enum ItemType {
	LICENSE,
	ATTRIBUTE;

	public boolean isLicense() {
		return this.equals(LICENSE);
	}

	public boolean isAttribute() {
		return this.equals(ATTRIBUTE);
	}
}
