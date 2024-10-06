package com.virnect.account.port.inbound;

import com.virnect.account.config.email.MailMessageContext;

public interface EmailService {
	void send(MailMessageContext mailMessageContext);

	String SYSTEM_EMAIL = "no-reply@squars.io";
}
