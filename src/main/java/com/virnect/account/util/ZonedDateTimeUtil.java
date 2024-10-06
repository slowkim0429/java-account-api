package com.virnect.account.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.virnect.account.security.SecurityUtil;

public class ZonedDateTimeUtil {

	public static final String DEFAULT_ZONE = "UTC";
	public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static ZonedDateTime zoneOffsetOfUTC() {
		ZoneId zoneId = ZoneId.of(DEFAULT_ZONE);
		return getOffsetOf(zoneId);
	}

	public static ZonedDateTime getOffsetOf(ZoneId zoneId) {
		return ZonedDateTime.now(zoneId);
	}

	public static ZonedDateTime getInfinitePeriod() {
		LocalDate expiredDate = LocalDate.of(9999, 12, 31);
		LocalTime expiredTime = LocalTime.of(11, 59);
		ZoneId zoneId = ZoneId.of(DEFAULT_ZONE);

		return ZonedDateTime.of(expiredDate, expiredTime, zoneId);
	}

	public static ZonedDateTime convertWithCurrentZoneId(ZonedDateTime zonedDateTime) {
		String currentZoneId = DEFAULT_ZONE;

		if (SecurityUtil.isAuthenticationUser()) {
			currentZoneId = SecurityUtil.getCurrentZoneId();
		}

		return zonedDateTime == null ? null : zonedDateTime.withZoneSameInstant(ZoneId.of(currentZoneId));
	}

	public static String convertToStringWithCurrentZoneId(ZonedDateTime zonedDateTime) {
		String currentZoneId = SecurityUtil.getCurrentZoneId();

		return zonedDateTime == null ? "" : zonedDateTime.withZoneSameInstant(ZoneId.of(currentZoneId))
			.format(DEFAULT_DATE_FORMATTER);
	}

	public static ZonedDateTime convertZonedDateTime(String datetimeString) {
		return ZonedDateTime.of(LocalDateTime.parse(datetimeString, DEFAULT_DATE_FORMATTER), ZoneId.of(DEFAULT_ZONE));
	}

	public static ZonedDateTime convertToZoneDateTime(LocalDate localDate, LocalTime localTime) {
		return ZonedDateTime.of(localDate.atTime(localTime), ZoneId.of(DEFAULT_ZONE));
	}

	public static String convertToString(ZonedDateTime zonedDateTime) {
		return zonedDateTime == null ? "" : zonedDateTime.withZoneSameInstant(ZoneId.of(DEFAULT_ZONE))
			.format(DEFAULT_DATE_FORMATTER);
	}

	public static ZonedDateTime convertToCurrentZonedDateTime(ZonedDateTime zonedDateTime) {
		ZoneId zoneId = ZoneId.of(SecurityUtil.getCurrentZoneId());
		ZoneOffset zoneOffset = zoneId.getRules().getStandardOffset(zonedDateTime.toInstant());
		long totalSeconds = zoneOffset.getTotalSeconds();
		return zonedDateTime.plusSeconds(totalSeconds);
	}
}
