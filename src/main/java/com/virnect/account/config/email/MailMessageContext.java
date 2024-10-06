package com.virnect.account.config.email;

import java.util.Locale;

import org.thymeleaf.context.Context;

import lombok.Builder;
import lombok.Getter;

import com.virnect.account.domain.enumclass.Mail;

@Getter
@Builder
public class MailMessageContext {
	private final Context mailContext;
	private final Mail mailType;
	private final String to;
	private final Locale locale = Locale.ENGLISH;
	private final String[] subjectArguments;

	@Override
	public String toString() {
		return "MailMessageContext{" +
			"mailType=" + mailType +
			", to='" + to + '\'' +
			", locale=" + locale +
			'}';
	}
}
