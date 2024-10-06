package com.virnect.account.adapter.inbound.dto.request.validate;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.virnect.account.domain.enumclass.ApprovalStatus;

@Target({PARAMETER, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ApprovalStatusSubsetValidator.class)
public @interface ApprovalStatusSubset {
	ApprovalStatus[] anyOf();
	String message() default "Approval Status Must Be Any Of {anyOf}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
