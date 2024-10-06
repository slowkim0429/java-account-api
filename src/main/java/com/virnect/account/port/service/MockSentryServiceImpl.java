package com.virnect.account.port.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.sentry.SentryEvent;
import lombok.extern.slf4j.Slf4j;

import com.virnect.account.adapter.outbound.request.SentrySendDto;
import com.virnect.account.port.inbound.SentryService;

@Service
@Profile(value = {"local", "test"})
@Slf4j
public class MockSentryServiceImpl implements SentryService {

	@Override
	public void sentryCaptureEvent(SentryEvent sentryEvent, SentrySendDto sentrySendDto) {
		log.info("MockSentryServiceImpl.sentryCaptureEvent");
	}
}
