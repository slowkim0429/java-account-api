package com.virnect.account.domain.enumclass;

public enum EventServiceType {
	SQUARS;

	public static EventServiceType nullableValueOf(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		return EventServiceType.valueOf(name);
	}
}
