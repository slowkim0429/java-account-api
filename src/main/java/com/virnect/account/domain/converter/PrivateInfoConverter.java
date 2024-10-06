package com.virnect.account.domain.converter;

import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

import com.virnect.account.log.NoLogging;
import com.virnect.account.security.AES256Util;

public class PrivateInfoConverter implements AttributeConverter<String, String> {
	@Override
	@NoLogging
	public String convertToDatabaseColumn(String attribute) {
		if(StringUtils.isEmpty(attribute)) {
			return "";
		}
		return AES256Util.encrypt(attribute);
	}

	@Override
	@NoLogging
	public String convertToEntityAttribute(String dbData) {
		if(StringUtils.isEmpty(dbData)) {
			return "";
		}
		return AES256Util.decrypt(dbData);
	}
}
