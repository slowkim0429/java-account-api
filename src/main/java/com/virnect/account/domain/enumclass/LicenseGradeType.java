package com.virnect.account.domain.enumclass;

public enum LicenseGradeType {
	FREE_PLUS,
	STANDARD,
	PROFESSIONAL,
	ENTERPRISE;

	public boolean isFree() {
		return this.equals(FREE_PLUS);
	}
}
