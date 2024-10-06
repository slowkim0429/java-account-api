package com.virnect.account.domain.enumclass;

public enum ExposureOptionType {
	UNUSE, SELECTIVE_DEACTIVATION_DAY, SELECTIVE_DEACTIVATION_NEVER;

	public static ExposureOptionType nullableValueOf(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		return ExposureOptionType.valueOf(name);
	}

	public boolean isSelectiveDeactivationDay() {
		return ExposureOptionType.SELECTIVE_DEACTIVATION_DAY.equals(this);
	}
}
