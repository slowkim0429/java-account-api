package com.virnect.account.config.redis;

import javax.servlet.http.HttpSession;

public class SessionUtil {
	private static final String LOGIN_USER_ID = "LOGIN_USER_ID";

	private SessionUtil() {
	}

	public static void setLoginUserId(HttpSession session, Long userId) {
		session.setAttribute(LOGIN_USER_ID, userId);
	}

	public static Long getLoginUserId(HttpSession session) {
		return (Long)session.getAttribute(LOGIN_USER_ID);
	}

	public static void logoutUserId(HttpSession session) {
		session.removeAttribute(LOGIN_USER_ID);
	}

	public static void clear(HttpSession session) {
		session.invalidate();
	}

	public static void logoutUser(HttpSession session) {
		session.removeAttribute(LOGIN_USER_ID);
	}
}
