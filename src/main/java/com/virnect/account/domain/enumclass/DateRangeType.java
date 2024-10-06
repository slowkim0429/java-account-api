package com.virnect.account.domain.enumclass;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

import com.virnect.account.util.ZonedDateTimeUtil;

public enum DateRangeType {
	ALL(() -> null, () -> null),
	WEEK(
		() -> ZonedDateTimeUtil.zoneOffsetOfUTC().with(LocalTime.MIN).minusWeeks(1),
		() -> ZonedDateTimeUtil.zoneOffsetOfUTC().with(LocalTime.MAX)
	),
	MONTH(
		() -> ZonedDateTimeUtil.zoneOffsetOfUTC().with(LocalTime.MIN).minusMonths(1),
		() -> ZonedDateTimeUtil.zoneOffsetOfUTC().with(LocalTime.MAX)
	),
	YEAR(
		() -> ZonedDateTimeUtil.zoneOffsetOfUTC().with(LocalTime.MIN).minusYears(1),
		() -> ZonedDateTimeUtil.zoneOffsetOfUTC().with(LocalTime.MAX)
	),
	CUSTOM(() -> null, () -> null);

	private final Supplier<ZonedDateTime> startDate;
	private final Supplier<ZonedDateTime> endDate;

	DateRangeType(Supplier<ZonedDateTime> startDate, Supplier<ZonedDateTime> endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public boolean isCustom() {
		return this.equals(CUSTOM);
	}

	public ZonedDateTime getStartDate() {
		return this.startDate.get();
	}

	public ZonedDateTime getEndDate() {
		return this.endDate.get();
	}
}
