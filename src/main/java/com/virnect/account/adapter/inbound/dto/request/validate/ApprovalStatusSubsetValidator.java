package com.virnect.account.adapter.inbound.dto.request.validate;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.virnect.account.domain.enumclass.ApprovalStatus;

public class ApprovalStatusSubsetValidator implements ConstraintValidator<ApprovalStatusSubset, String> {
	private ApprovalStatus[] anyOfApprovalStatus;

	@Override
	public void initialize(ApprovalStatusSubset constraintAnnotation) {
		this.anyOfApprovalStatus = constraintAnnotation.anyOf();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.equals("")) {
			return true;
		}
		return Arrays.stream(anyOfApprovalStatus).anyMatch(approvalStatus -> approvalStatus.name().equals(value));
	}
}
