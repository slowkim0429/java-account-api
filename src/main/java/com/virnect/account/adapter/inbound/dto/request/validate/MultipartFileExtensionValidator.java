package com.virnect.account.adapter.inbound.dto.request.validate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileExtensionValidator implements ConstraintValidator<MultipartFileExtension, MultipartFile> {
	private List<String> allowedExtensions;
	private String errorMessage;

	@Override
	public void initialize(MultipartFileExtension constraintAnnotation) {
		this.allowedExtensions = Arrays.stream(constraintAnnotation.value()).map(FileExtensionType::name).collect(
			Collectors.toList());
		this.errorMessage = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		if (value == null || value.getSize() == 0) {
			return true;
		}
		context.buildConstraintViolationWithTemplate(this.errorMessage).addConstraintViolation();
		String extension = FilenameUtils.getExtension(value.getOriginalFilename());
		return allowedExtensions.contains(extension.toUpperCase());
	}
}
