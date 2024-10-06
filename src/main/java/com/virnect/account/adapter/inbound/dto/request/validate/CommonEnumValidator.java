package com.virnect.account.adapter.inbound.dto.request.validate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CommonEnumValidator implements ConstraintValidator<CommonEnum, CharSequence> {
	private List<String> commonEnumConstants;

	@Override
	public void initialize(CommonEnum annotation) {
		commonEnumConstants = Stream.of(annotation.enumClass().getEnumConstants())
			.map(java.lang.Enum::name)
			.collect(Collectors.toList());
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		if (value == null || value.equals("")) {
			return true;
		}
		return commonEnumConstants.contains(value.toString());
	}
}
