package com.virnect.account.adapter.inbound.dto.request.validate;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.virnect.account.domain.enumclass.RecurringIntervalType;

public class RecurringIntervalTypeSubsetValidator implements ConstraintValidator<RecurringIntervalTypeSubset, String> {
	private RecurringIntervalType[] anyOfRecurringIntervalType;

	@Override
	public void initialize(RecurringIntervalTypeSubset constraintAnnotation) {
		this.anyOfRecurringIntervalType = constraintAnnotation.anyOf();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return Arrays.stream(anyOfRecurringIntervalType).anyMatch(recurringIntervalType -> recurringIntervalType.name().equalsIgnoreCase(value));
	}
}
