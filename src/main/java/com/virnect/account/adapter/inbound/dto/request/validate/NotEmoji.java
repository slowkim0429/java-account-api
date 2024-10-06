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
@Constraint(validatedBy = NotEmojiValidator.class)
public @interface NotEmoji {
	String message() default "이모지를 입력할 수 없습니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
