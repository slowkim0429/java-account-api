package com.virnect.account.adapter.inbound.dto.request.validate;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({PARAMETER, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CommonEnumValidator.class)
public @interface CommonEnum {
	Class<? extends java.lang.Enum<?>> enumClass();

	String message() default "must be any enum {enumClass}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
