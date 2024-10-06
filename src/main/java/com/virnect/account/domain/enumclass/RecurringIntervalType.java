package com.virnect.account.domain.enumclass;

public enum RecurringIntervalType {
	NONE,
	MONTH,
	YEAR;

	public boolean isMonth() {
		return this.equals(MONTH);
	}

	public boolean isYear() {
		return this.equals(YEAR);
	}

	public boolean isNone() {
		return this.equals(NONE);
	}

	public boolean isNotNone() {
		return !isNone();
	}
}
