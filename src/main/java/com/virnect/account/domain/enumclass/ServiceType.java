package com.virnect.account.domain.enumclass;

public enum ServiceType {
	WORKSPACE, SQUARS;

	public boolean isWorkspace() {
		return this.equals(WORKSPACE);
	}

	public boolean isSquars() {
		return this.equals(SQUARS);
	}
}
