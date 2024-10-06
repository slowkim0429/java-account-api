package com.virnect.account.port.inbound;

import io.sentry.SentryEvent;

import com.virnect.account.adapter.outbound.request.SentrySendDto;

public interface SentryService {
	void sentryCaptureEvent(SentryEvent sentryEvent, SentrySendDto sentrySendDto);
}
