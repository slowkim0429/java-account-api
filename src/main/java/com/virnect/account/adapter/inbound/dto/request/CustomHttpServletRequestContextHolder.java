package com.virnect.account.adapter.inbound.dto.request;

public class CustomHttpServletRequestContextHolder {
	public static ThreadLocal<CustomHttpServletRequest> httpServletRequestContext = new ThreadLocal<>();

}
