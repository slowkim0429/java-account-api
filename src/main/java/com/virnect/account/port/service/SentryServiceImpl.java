package com.virnect.account.port.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.sentry.Sentry;
import io.sentry.SentryEvent;

import com.virnect.account.adapter.outbound.request.SentrySendDto;
import com.virnect.account.port.inbound.SentryService;

@Service
@Profile(value = {"develop", "qa", "staging", "production"})
public class SentryServiceImpl implements SentryService {
	@Override
	public void sentryCaptureEvent(SentryEvent sentryEvent, SentrySendDto sentrySendDto) {
		sentryEvent.setExtra("service", sentrySendDto.getService());
		sentryEvent.setExtra("originType", sentrySendDto.getOriginType());
		sentryEvent.setExtra("clientServiceName", sentrySendDto.getClientServiceName());
		sentryEvent.setExtra("url", sentrySendDto.getUrl());
		sentryEvent.setExtra("queryString", sentrySendDto.getQueryString());
		sentryEvent.setExtra("method", sentrySendDto.getMethod());
		sentryEvent.setExtra("controller", sentrySendDto.getController());
		sentryEvent.setExtra("methodName", sentrySendDto.getMethodName());
		sentryEvent.setExtra("requestBody", sentrySendDto.getRequestBody());
		sentryEvent.setExtra("responseBody", sentrySendDto.getResponseBody());
		sentryEvent.setExtra("responseStatus", sentrySendDto.getResponseStatus());
		sentryEvent.setExtra("thirdPartyStackTrace", sentrySendDto.getThirdPartyStackTrace());
		sentryEvent.setExtra("elapsedTime", sentrySendDto.getElapsedTime());
		Sentry.captureEvent(sentryEvent);
	}
}
