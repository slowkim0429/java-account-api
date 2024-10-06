package com.virnect.account.adapter.inbound.dto.request.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.virnect.account.domain.enumclass.AcceptOrReject;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AcceptOrRejectSubsetValidator.class)
public @interface AcceptOrRejectSubset {
	AcceptOrReject[] anyOf();
	String message() default "Must Be Any Of {anyOf}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
