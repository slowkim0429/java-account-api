package com.virnect.account.domain.enumclass;

public enum OrganizationStatus {
	REGISTER,
	REVIEWING,
	PENDING,
	REJECT,
	APPROVED,
	DELETE;

	public boolean isApproved() {
		return this.equals(APPROVED);
	}
}
