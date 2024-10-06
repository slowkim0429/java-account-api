package com.virnect.account.adapter.inbound.dto.request.validate;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.virnect.account.domain.enumclass.UseStatus;

public class UseStatusSubsetValidator implements ConstraintValidator<UseStatusSubset, String> {
	private UseStatus[] anyOfUseStatus;

	@Override
	public void initialize(UseStatusSubset constraintAnnotation) {
		this.anyOfUseStatus = constraintAnnotation.anyOf();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return Arrays.stream(anyOfUseStatus).anyMatch(useStatus -> useStatus.name().equals(value));

	}
}
