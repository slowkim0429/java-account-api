package com.virnect.account.domain.converter;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.util.StringUtils;

public class ValueConverter {
	private ValueConverter() {
	}

	public static String getName(String lastName, String firstName) {
		if (StringUtils.isEmpty(lastName) && StringUtils.isEmpty(firstName)) {
			return "";
		}
		return lastName + firstName;
	}

	public static String getNickname(String nickname, String lastName, String firstName) {
		if (StringUtils.hasText(nickname)) {
			return nickname;
		}
		return lastName + firstName;
	}

	public static String getTelNumber(String telNumber) {
		if (StringUtils.isEmpty(telNumber)) {
			return "";
		}
		return telNumber.replaceAll("\\+\\d{1,6}\\S[-| ]", "").replace("-", "").replace(" ", "");
	}

	public static String getInternationalNumberOfTelNumber(String telNumber) {
		if (StringUtils.isEmpty(telNumber)) {
			return "";
		}

		if (telNumber.indexOf("-") > 0) {
			return telNumber.split("-")[0];
		}

		if (telNumber.indexOf(" ") > 0) {
			return telNumber.split(" ")[0];
		}

		return "";
	}

	public static String getMobile(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
			return "";
		}
		return mobile.replaceAll("\\+\\d{1,6}\\S[-| ]", "").replace("-", "").replace(" ", "");
	}

	public static String getInternationalNumberOfMobile(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
			return "";
		}

		if (mobile.indexOf("-") > 0) {
			return mobile.split("-")[0];
		}

		if (mobile.indexOf(" ") > 0) {
			return mobile.split(" ")[0];
		}

		return "";
	}

	public static String getEmailDomain(String email) {
		return email.substring(email.indexOf("@") + 1);
	}

	public static String getStackTraceString(Exception e) {
		StringWriter stackTraceContent = new StringWriter();
		e.printStackTrace(new PrintWriter(stackTraceContent));
		return stackTraceContent.toString();
	}

}
