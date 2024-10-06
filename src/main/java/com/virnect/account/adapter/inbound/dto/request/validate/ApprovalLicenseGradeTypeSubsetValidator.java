package com.virnect.account.adapter.inbound.dto.request.validate;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.virnect.account.domain.enumclass.LicenseGradeType;

public class ApprovalLicenseGradeTypeSubsetValidator
	implements ConstraintValidator<ApprovalLicenseGradeTypeSubset, String> {
	private LicenseGradeType[] anyOfApprovalLicenseGradeType;

	@Override
	public void initialize(ApprovalLicenseGradeTypeSubset constraintAnnotation) {
		this.anyOfApprovalLicenseGradeType = constraintAnnotation.anyOf();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.equals("")) {
			return true;
		}
		return Arrays.stream(anyOfApprovalLicenseGradeType)
			.anyMatch(licenseGradeType -> licenseGradeType.name().equals(value));
	}
}
