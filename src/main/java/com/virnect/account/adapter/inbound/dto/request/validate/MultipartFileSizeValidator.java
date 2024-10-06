package com.virnect.account.adapter.inbound.dto.request.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class MultipartFileSizeValidator implements ConstraintValidator<MultipartFileSize, MultipartFile> {

	private Long limitFileSize;
	private String errorMessage;

	@Override
	public void initialize(MultipartFileSize constraintAnnotation) {
		long fileSize = constraintAnnotation.value();
		UnitType unit = constraintAnnotation.unit();
		errorMessage = constraintAnnotation.message();
		limitFileSize = fileSize * unit.volume;
	}

	@Override
	public boolean isValid(final MultipartFile value, final ConstraintValidatorContext context) {
		if (value == null || value.getSize() == 0) {
			return true;
		}
		context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
		return value.getSize() <= limitFileSize;
	}
}
