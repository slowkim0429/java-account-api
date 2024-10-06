package com.virnect.account.port.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.virnect.account.config.email.MailMessageContext;
import com.virnect.account.port.inbound.EmailService;

@Component
@Profile(value = {"test"})
@Slf4j
public class TestEmailServiceImpl implements EmailService {
	@Override
	public void send(MailMessageContext mailMessageContext) {
		log.info("TestEmailServiceImpl - mailMessageContext = " + mailMessageContext.toString());
	}
}

