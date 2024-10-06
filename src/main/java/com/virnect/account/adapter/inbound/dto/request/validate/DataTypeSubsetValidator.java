package com.virnect.account.adapter.inbound.dto.request.validate;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.virnect.account.domain.enumclass.DataType;

public class DataTypeSubsetValidator implements ConstraintValidator<DataTypeSubset, String> {
	private DataType[] anyOfDataType;

	@Override
	public void initialize(DataTypeSubset constraintAnnotation) {
		this.anyOfDataType = constraintAnnotation.anyOf();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return Arrays.stream(anyOfDataType).anyMatch(dataType -> dataType.name().equals(value));
	}
}
