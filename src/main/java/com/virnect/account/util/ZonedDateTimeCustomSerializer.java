package com.virnect.account.util;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeCustomSerializer extends JsonSerializer<ZonedDateTime> {

	@Override
	public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws
		IOException {
		gen.writeString(convertZonedDateTime(value));
	}

	private String convertZonedDateTime(ZonedDateTime value) {
		ZonedDateTime zonedDateTime = ZonedDateTimeUtil.convertWithCurrentZoneId(value);

		if (zonedDateTime == null) {
			return null;
		}

		return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
}