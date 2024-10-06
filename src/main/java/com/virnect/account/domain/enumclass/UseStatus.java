package com.virnect.account.domain.enumclass;

public enum UseStatus {
	NONE,
	USE,
	UNUSE,
	DELETE;

	public boolean isUse() {
		return this.equals(USE);
	}

	public boolean isNotUse() {
		return !this.isUse();
	}

	public boolean isDelete() {
		return this.equals(DELETE);
	}

	public boolean isUnuse() {
		return this.equals(UNUSE);
	}
}
