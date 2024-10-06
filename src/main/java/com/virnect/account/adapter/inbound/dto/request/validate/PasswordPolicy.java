package com.virnect.account.adapter.inbound.dto.request.validate;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target({PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordPolicyValidator.class)
public @interface PasswordPolicy {
	String message() default "Not Matched Password Policy";

	int minLength() default 0;

	int maxLength() default Integer.MAX_VALUE;

	int maxNumberOfRepeatedLetter() default Integer.MAX_VALUE;

	boolean caseSensitive() default true;
}
