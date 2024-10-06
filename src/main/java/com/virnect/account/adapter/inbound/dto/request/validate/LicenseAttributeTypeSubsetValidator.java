package com.virnect.account.adapter.inbound.dto.request.validate;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.virnect.account.domain.enumclass.LicenseAttributeType;

public class LicenseAttributeTypeSubsetValidator implements ConstraintValidator<LicenseAttributeTypeSubset, String> {
	private LicenseAttributeType[] anyOfLicenseAttributeType;

	@Override
	public void initialize(LicenseAttributeTypeSubset constraintAnnotation) {
		this.anyOfLicenseAttributeType = constraintAnnotation.anyOf();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return Arrays.stream(anyOfLicenseAttributeType).anyMatch(licenseAttributeType -> licenseAttributeType.name().equals(value));
	}
}
