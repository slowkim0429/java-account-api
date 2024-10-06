package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.AuthCodeVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.password.EmailChangePasswordRequestDto;
import com.virnect.account.config.email.MailMessageContext;
import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.model.Domain;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.DomainService;
import com.virnect.account.port.inbound.EmailAuthAdminService;
import com.virnect.account.port.inbound.EmailService;
import com.virnect.account.port.outbound.AdminUserRepository;
import com.virnect.account.port.outbound.RedisRepository;

@Service
@RequiredArgsConstructor
public class EmailAuthAdminServiceImpl implements EmailAuthAdminService {

	private final AdminUserRepository adminUserRepository;
	private final RedisRepository redisRepository;
	private final EmailService emailService;
	private final DomainService domainService;
	private static final String ADMIN_DOMAIN_RECORD_NAME = "ADMIN";
	private static final long EMAIL_AUTHENTICATION_DURATION = Duration.ofMinutes(30).getSeconds();

	@Override
	public void sendResetPasswordEmailAuthCode(EmailChangePasswordRequestDto emailChangePasswordRequestDto) {
		String receiverEmail = emailChangePasswordRequestDto.getLowerCaseEmail();

		if (adminUserRepository.getAdminUser(receiverEmail).isEmpty()) {
			throw new CustomException(NOT_FOUND_ADMIN_USER);
		}

		String authCode = RandomStringUtils.randomNumeric(6);

		redisRepository.setAdminResetPasswordAsSecond(
			emailChangePasswordRequestDto.getEmail(), authCode, EMAIL_AUTHENTICATION_DURATION);

		MailMessageContext authenticationEmailContext = createAuthenticationEmailContext(
			receiverEmail, authCode, Mail.AUTH_CODE);

		emailService.send(authenticationEmailContext);
	}

	@Override
	public void verifyResetPasswordEmailAuthCode(AuthCodeVerificationRequestDto requestDto) {
		String authCode = redisRepository.getAdminResetPassword(requestDto.getEmail());

		if (authCode == null) {
			throw new CustomException(EXPIRED_EMAIL_SESSION_CODE);
		}

		if (!Objects.equals(requestDto.getCode(), authCode)) {
			throw new CustomException(INVALID_EMAIL_AUTH_CODE);
		}
	}

	@Override
	public void sendAdminAccountApplyEmail(
		Long applyUserRegionId, String applyUserEmail, String adminMasterUserEmail, String applyUserNickname
	) {
		MailMessageContext mailMessageContext = createAdminAccountApplyEmailContext(
			applyUserRegionId, applyUserEmail, adminMasterUserEmail, applyUserNickname);
		emailService.send(mailMessageContext);
	}

	@Override
	public void sendAdminAccountApprovedEmail(
		Long approvedUserRegionId, String approvedUserEmail, String approvedUserNickname, ZonedDateTime approevdDate
	) {
		MailMessageContext mailMessageContext = createAdminAccountApprovedEmailContext(
			approvedUserRegionId, approvedUserEmail, approvedUserNickname, approevdDate);
		emailService.send(mailMessageContext);
	}

	private MailMessageContext createAuthenticationEmailContext(
		String receiverEmail, String authCode, Mail mailType
	) {
		Context context = new Context();
		context.setVariable("email", receiverEmail);
		context.setVariable("code", authCode);

		return MailMessageContext.builder()
			.mailContext(context)
			.mailType(mailType)
			.to(receiverEmail)
			.build();
	}

	private MailMessageContext createAdminAccountApprovedEmailContext(
		Long approvedUserRegionId, String approvedUserEmail, String approvedUserNickname, ZonedDateTime approvedDate
	) {
		Domain adminDomain = domainService.getDomain(approvedUserRegionId, ADMIN_DOMAIN_RECORD_NAME);

		Context context = new Context();
		context.setVariable("approvedUserEmail", approvedUserEmail);
		context.setVariable("approvedUserNickname", approvedUserNickname);
		context.setVariable("approvedDate", dateFormat(approvedDate));
		context.setVariable("adminPageUrl", adminDomain.getUrl());

		return MailMessageContext.builder()
			.mailContext(context)
			.mailType(Mail.ADMIN_ACCOUNT_APPROVED)
			.to(approvedUserEmail)
			.build();
	}

	private MailMessageContext createAdminAccountApplyEmailContext(
		Long applyUserRegionId, String applyUserEmail, String adminMasterUserEmail, String applyUserNickname
	) {
		Domain adminDomain = domainService.getDomain(applyUserRegionId, ADMIN_DOMAIN_RECORD_NAME);

		Context context = new Context();
		context.setVariable("applyUserEmail", applyUserEmail);
		context.setVariable("applyUserNickname", applyUserNickname);
		context.setVariable("adminPageUrl", adminDomain.getUrl());

		return MailMessageContext.builder()
			.mailContext(context)
			.mailType(Mail.ADMIN_ACCOUNT_APPLY)
			.to(adminMasterUserEmail)
			.build();
	}

	private String dateFormat(ZonedDateTime zonedDateTime) {
		return zonedDateTime == null ? "" :
			zonedDateTime.format(DateTimeFormatter.ofPattern("dd MMM, yyyy").withLocale(Locale.ENGLISH));
	}
}
