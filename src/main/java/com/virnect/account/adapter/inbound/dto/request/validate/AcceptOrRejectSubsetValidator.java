package com.virnect.account.adapter.inbound.dto.request.validate;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.virnect.account.domain.enumclass.AcceptOrReject;

public class AcceptOrRejectSubsetValidator implements ConstraintValidator<AcceptOrRejectSubset, String> {
	AcceptOrReject[] anyOfAcceptOrReject;

	@Override
	public void initialize(AcceptOrRejectSubset constraintAnnotation) {
		this.anyOfAcceptOrReject = constraintAnnotation.anyOf();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return Arrays.stream(anyOfAcceptOrReject).anyMatch(acceptOrReject -> acceptOrReject.name().equals(value));
	}
}
