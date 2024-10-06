package com.virnect.account.port.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;

import com.virnect.account.config.email.MailMessageContext;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.EmailService;
import com.virnect.account.port.outbound.RedisRepository;

@Component
@RequiredArgsConstructor
@Profile(value = {"local", "develop", "qa", "staging", "production"})
public class EmailTemplateServiceImpl implements EmailService {
	private final MessageSource messageSource;
	private final SpringTemplateEngine springTemplateEngine;
	private final JavaMailSender javaMailSender;

	private final RedisRepository redisRepository;

	@Async("asyncTaskExecutor")
	@Override
	public void send(final MailMessageContext mailMessageContext) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			Context context = mailMessageContext.getMailContext();
			String subject;

			if (mailMessageContext.getSubjectArguments() != null
				&& mailMessageContext.getSubjectArguments().length > 0) {
				subject = messageSource.getMessage(
					mailMessageContext.getMailType().getSubject(), mailMessageContext.getSubjectArguments(),
					mailMessageContext.getLocale()
				);
			} else {
				subject = messageSource.getMessage(
					mailMessageContext.getMailType().getSubject(), null, mailMessageContext.getLocale());
			}

			String template = messageSource.getMessage(
				mailMessageContext.getMailType().getTemplate(), null, mailMessageContext.getLocale());
			String html = springTemplateEngine.process(template, context);

			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setFrom(SYSTEM_EMAIL);
			mimeMessageHelper.setTo(mailMessageContext.getTo());
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(html, true);
			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			CustomException customException = new CustomException(ErrorCode.MAIL_SEND_ERROR);
			customException.initCause(e);
			redisRepository.deleteObjectValue(mailMessageContext.getTo());
			throw customException;
		}
	}
}
