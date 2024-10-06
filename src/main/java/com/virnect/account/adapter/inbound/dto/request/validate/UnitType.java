package com.virnect.account.adapter.inbound.dto.request.validate;

import lombok.Getter;

@Getter
public enum UnitType {
	KB(1024), MB(1024 * 1024), GB(1024 * 1024 * 1024);

	UnitType(long volume) {
		this.volume = volume;
	}

	public final long volume;
}
