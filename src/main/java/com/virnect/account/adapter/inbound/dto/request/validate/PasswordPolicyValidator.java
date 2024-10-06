package com.virnect.account.adapter.inbound.dto.request.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordPolicyValidator implements ConstraintValidator<PasswordPolicy, String> {
	private static final String ALLOWED_LETTER_CHECK_REGEXP = "[a-zA-Z0-9.!@#$%]+";
	private static final String REPEATED_FOUR_TIME_LETTER_CHECK_REGEXP = "(\\w)\\1{3,}";
	private static final String SEQUENTIAL_NUMBER_CHECK_REGEXP = "^.*(0123|1234|2345|3456|4567|5678|6789|7890).*$";
	private int minLength;
	private int maxLength;
	private int maxNumberOfRepeatedLetter;
	private boolean isCaseSensitive;

	@Override
	public void initialize(PasswordPolicy passwordPolicyAnnotation) {
		ConstraintValidator.super.initialize(passwordPolicyAnnotation);
		minLength = passwordPolicyAnnotation.minLength();
		maxLength = passwordPolicyAnnotation.maxLength();
		maxNumberOfRepeatedLetter = passwordPolicyAnnotation.maxNumberOfRepeatedLetter();
		isCaseSensitive = passwordPolicyAnnotation.caseSensitive();
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password.length() < minLength || password.length() > maxLength) {
			return false;
		}

		if (!password.matches(ALLOWED_LETTER_CHECK_REGEXP)) {
			return false;
		}

		if (password.matches(SEQUENTIAL_NUMBER_CHECK_REGEXP)) {
			return false;
		}

		if (isSequentialLetter(password))
			return false;

		return password.matches(REPEATED_FOUR_TIME_LETTER_CHECK_REGEXP);
	}

	private boolean isSequentialLetter(String password) {
		if (!isCaseSensitive) {
			password = password.toLowerCase();
		}
		int count = 0;
		for (int i = 0; i < password.length() - 1; i++) {
			if (!Character.isAlphabetic(password.charAt(i)) || password.charAt(i) - password.charAt(i + 1) != -1) {
				count = 0;
				continue;
			}
			count++;
			if (count > maxNumberOfRepeatedLetter) {
				return true;
			}
		}
		return false;
	}
}
