package com.virnect.account.adapter.inbound.dto.request.validate;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.virnect.account.domain.enumclass.LicenseAttributeType;

@Target({PARAMETER, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LicenseAttributeTypeSubsetValidator.class)
public @interface LicenseAttributeTypeSubset {
	LicenseAttributeType[] anyOf();
	String message() default "License Attribute Type Must Be Any Of {anyOf}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
