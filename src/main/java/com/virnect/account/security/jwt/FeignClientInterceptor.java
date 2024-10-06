package com.virnect.account.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
	@Value("${slack.webhook-url:https://hooks.slack.com}")
	private String slackWebhookUrl;

	public static String getBearerTokenHeader() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest()
			.getHeader(HttpHeaders.AUTHORIZATION);
	}

	@Override
	public void apply(RequestTemplate requestTemplate) {
		if (requestTemplate.feignTarget().url().equals(slackWebhookUrl)) {
			return;
		}

		if (requestTemplate.headers().get(HttpHeaders.AUTHORIZATION) == null) {
			requestTemplate.header(HttpHeaders.AUTHORIZATION, getBearerTokenHeader());
		}
	}
}
