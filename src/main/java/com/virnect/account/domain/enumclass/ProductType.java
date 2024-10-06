package com.virnect.account.domain.enumclass;

public enum ProductType {
	SQUARS,
	TRACK;

	public boolean isTrack() {
		return this.equals(TRACK);
	}

	public boolean isSquars() {
		return this.equals(SQUARS);
	}

	public boolean isProductTypeWithAttribute() {
		return this.equals(SQUARS);
	}

	public boolean isTargetOfSynchronization() {
		return this.equals(SQUARS);
	}
}
