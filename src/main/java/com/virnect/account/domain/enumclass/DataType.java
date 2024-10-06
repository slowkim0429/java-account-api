package com.virnect.account.domain.enumclass;

public enum DataType {
	NUMBER,
	BOOL;

	public static DataType nullableValueOf(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		return DataType.valueOf(name);
	}

	public boolean isNumber() {
		return DataType.NUMBER.equals(this);
	}
}
