package com.virnect.account.domain.enumclass;

public enum MembershipStatus {
	JOIN,
	RESIGN;

	public boolean isResigned() {
		return MembershipStatus.RESIGN.equals(this);
	}
}