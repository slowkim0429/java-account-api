package com.virnect.account.adapter.inbound.dto.request.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

public class NotEmojiValidator implements ConstraintValidator<NotEmoji, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isBlank(value)) {
			return true;
		}
		return isContainsSurrogateChar(value);
	}

	private boolean isContainsSurrogateChar(String text) {
		for (int i = 0; i < text.length(); i++) {
			if (Character.isSurrogate(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
