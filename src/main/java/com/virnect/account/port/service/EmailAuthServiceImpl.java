package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.AuthCodeVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.EmailAuthRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.password.EmailChangePasswordRequestDto;
import com.virnect.account.config.email.MailMessageContext;
import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.enumclass.CouponBenefitOption;
import com.virnect.account.domain.enumclass.CouponLicenseGradeMatchingType;
import com.virnect.account.domain.enumclass.CouponRecurringIntervalMatchingType;
import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.model.Domain;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.User;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.DomainService;
import com.virnect.account.port.inbound.EmailAuthService;
import com.virnect.account.port.inbound.EmailManagementService;
import com.virnect.account.port.inbound.EmailService;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.UserRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.util.ZonedDateTimeUtil;

@Component
@RequiredArgsConstructor
public class EmailAuthServiceImpl implements EmailAuthService {
	private static final String LOGIN_DOMAIN_RECORD_NAME = "LOGIN";
	private static final String COUPON_LICENSE_GRADE_NONE_TYPE = "STANDARD / PROFESSIONAL";
	private static final String COUPON_RECURRING_INTERVAL_NONE_TYPE = "MONTH / YEAR";
	private final EmailService emailService;
	private final DomainService domainService;
	private final EmailManagementService emailManagementService;

	private final UserRepository userRepository;
	private final RedisRepository redisRepository;
	private final long EMAIL_AUTHENTICATION_DURATION = Duration.ofMinutes(30).getSeconds();
	@Value("${mail.invite-url:http://localhost:8883/invite}")
	private String inviteUrl;

	@Value("${mail.redirect-url:https://squars.io}")
	private String redirectUrl;

	@Override
	public void emailAuthentication(EmailAuthRequestDto requestDto) {
		String receiverEmail = requestDto.getLowerCaseEmail();

		if (userRepository.existsUserByEmail(receiverEmail)) {
			throw new CustomException(DUPLICATE_USER);
		}

		String authCode = RandomStringUtils.randomNumeric(6);

		redisRepository.setObjectValueAsSecond(receiverEmail, authCode, EMAIL_AUTHENTICATION_DURATION);

		MailMessageContext authenticationEmailContext = createAuthenticationEmailContext(
			receiverEmail, authCode, Mail.AUTH_CODE);

		emailService.send(authenticationEmailContext);
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

	@Override
	public void emailAuthCodeVerification(AuthCodeVerificationRequestDto requestDto) {
		String authCode = redisRepository.getStringValue(requestDto.getEmail());

		if (authCode == null) {
			throw new CustomException(EXPIRED_EMAIL_SESSION_CODE);
		}

		if (!Objects.equals(requestDto.getCode(), authCode)) {
			throw new CustomException(INVALID_EMAIL_AUTH_CODE);
		}
	}

	public void sendSignupWelcomeEmail(User user) {
		MailMessageContext signUpWelcomeEmailContext = createSignUpWelcomeEmailContext(user);
		emailService.send(signUpWelcomeEmailContext);
	}

	@Override
	public void sendChangePasswordAuthEmail(
		EmailChangePasswordRequestDto requestDto
	) {
		String receiverEmail = requestDto.getEmail();

		if (!userRepository.existsUserByEmail(receiverEmail)) {
			throw new CustomException(NOT_FOUND_USER);
		}

		String authCode = RandomStringUtils.randomNumeric(6);

		redisRepository.setObjectValueAsSecond(receiverEmail, authCode, EMAIL_AUTHENTICATION_DURATION);

		MailMessageContext changePasswordEmailContext = createAuthenticationEmailContext(
			receiverEmail, authCode, Mail.AUTH_CODE);

		emailService.send(changePasswordEmailContext);
	}

	@Override
	public void emailAuthCodeVerification(
		UpdatePasswordRequestDto updatePasswordRequestDto
	) {
		AuthCodeVerificationRequestDto authCodeVerificationRequestDto = new AuthCodeVerificationRequestDto();
		authCodeVerificationRequestDto.setEmail(updatePasswordRequestDto.getEmail());
		authCodeVerificationRequestDto.setCode(updatePasswordRequestDto.getCode());

		emailAuthCodeVerification(authCodeVerificationRequestDto);
	}

	@Override
	public void sendItemPaymentLinkEmail(User user, Item item) {
		String url = String.format("%s/subscriptions", redirectUrl);
		String fullRedirectUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);

		MailMessageContext itemPaymentLinkEmailContext = createItemPaymentLinkEmailContext(
			user, item, fullRedirectUrl);
		emailService.send(itemPaymentLinkEmailContext);
	}

	public MailMessageContext createItemPaymentLinkEmailContext(
		User user, Item item, String fullRedirectUrl
	) {
		Domain loginDomain = domainService.getDomain(user.getRegionId(), LOGIN_DOMAIN_RECORD_NAME);

		Context context = new Context();
		context.setVariable("userNickname", user.getNickname());
		context.setVariable("itemName", item.getName());
		context.setVariable("itemRecurringIntervalType", item.getRecurringInterval());
		context.setVariable("itemCurrencyType", item.getCurrencyType());
		context.setVariable("itemAmount", priceFormat(item.getAmount()));
		context.setVariable("itemPaymentPageUrl", loginDomain.getUrl());
		context.setVariable("itemId", item.getId());
		context.setVariable("fullRedirectUrl", fullRedirectUrl);

		return MailMessageContext.builder()
			.mailContext(context)
			.mailType(Mail.ITEM_PAYMENT_LINK)
			.to(user.getEmail())
			.build();
	}

	private MailMessageContext createSignUpWelcomeEmailContext(User user) {
		Context context = new Context();
		context.setVariable("email", user.getEmail());
		context.setVariable("name", user.getNickname());
		context.setVariable("nickname", user.getNickname());
		context.setVariable("registerDate", dateFormat(user.getCreatedDate()));
		context.setVariable("timeZoneInfo", getTimeZoneFormatString());

		emailManagementService.getUsingEmailManagement(Mail.WELCOME)
			.ifPresent(emailCustomizingManagement -> context.setVariable(
				"emailCustomizingImageUrl",
				emailCustomizingManagement.getContentsInlineImageUrl()
			));

		return MailMessageContext.builder()
			.mailContext(context)
			.mailType(Mail.WELCOME)
			.to(user.getEmail())
			.build();
	}

	@Override
	public void sendUserResignEmail(String nickname, String email) {
		MailMessageContext mailMessageContext = createUserResignEmailContext(nickname, email);
		emailService.send(mailMessageContext);
	}

	public MailMessageContext createUserResignEmailContext(String nickname, String email) {
		Context context = new Context();
		context.setVariable("nickname", nickname);
		context.setVariable("email", email);
		context.setVariable("resignDate", dateFormat(ZonedDateTimeUtil.zoneOffsetOfUTC()));
		context.setVariable("timeZoneInfo", getTimeZoneFormatString());

		return MailMessageContext.builder()
			.mailContext(context)
			.mailType(Mail.USER_RESIGN)
			.to(email)
			.build();
	}

	@Override
	public void sendGroupUserInviteEmail(
		String currentUserEmail, String currentUserNickname, String receiverEmail, String groupName,
		String profileColor, Long workspaceId, Long groupId, String inviteToken, String localeCode
	) {
		String fullRedirectUrl = String.format(
			"%s/%d/group/%d", redirectUrl, workspaceId, groupId);

		MailMessageContext mailMessageContext = createGroupUserInviteMailContext(
			currentUserEmail, currentUserNickname, receiverEmail, groupName, profileColor, localeCode,
			fullRedirectUrl, inviteToken
		);

		emailService.send(mailMessageContext);
	}

	private MailMessageContext createGroupUserInviteMailContext(
		String currentUserEmail, String currentUserNickname, String receiverEmail, String groupName,
		String profileColor,
		String localeCode, String fullRedirectUrl, String inviteToken
	) {
		Context context = new Context();
		context.setVariable("currentUserEmail", currentUserEmail);
		context.setVariable("currentUserNickname", currentUserNickname);
		context.setVariable("receiverEmail", receiverEmail);
		context.setVariable("groupName", groupName);
		context.setVariable("colorUrl", Color.valueOf(profileColor).getColorUrl());
		context.setVariable("inviteUrl", inviteUrl);
		context.setVariable("fullRedirectUrl", fullRedirectUrl);
		context.setVariable("inviteToken", inviteToken);
		context.setVariable("localeCode", localeCode);

		return MailMessageContext.builder()
			.mailContext(context)
			.mailType(Mail.GROUP_USER_INVITE)
			.to(receiverEmail)
			.subjectArguments(new String[] {groupName})
			.build();
	}

	@Override
	public void sendOrganizationLicenseKeyEmail(String email, String licenseKey) {
		MailMessageContext mailMessageContext = createOrganizationLicenseKeyMailContext(email, licenseKey);
		emailService.send(mailMessageContext);
	}

	private MailMessageContext createOrganizationLicenseKeyMailContext(String email, String licenseKey) {
		Context context = new Context();
		context.setVariable("licenseKey", licenseKey);

		return MailMessageContext.builder()
			.mailContext(context)
			.mailType(Mail.ORGANIZATION_LICENSE_KEY)
			.to(email)
			.build();
	}

	@Override
	public void sendCouponEmail(
		String emailTitle, String emailContentInlineImageUrl, String code,
		String couponLicenseGradeMatchingType,
		String couponRecurringIntervalMatchingType, ZonedDateTime expiredDate,
		CouponBenefitOption benefitOption, Long benefitValue, String receiverEmail
	) {
		String fullRedirectUrl = String.format(
			"%s/subscriptions?recurringInterval=%s", redirectUrl, couponRecurringIntervalMatchingType);

		if (CouponLicenseGradeMatchingType.NONE.name().equals(couponLicenseGradeMatchingType)) {
			couponLicenseGradeMatchingType = COUPON_LICENSE_GRADE_NONE_TYPE;
		}

		if (CouponRecurringIntervalMatchingType.NONE.name().equals(couponRecurringIntervalMatchingType)) {
			couponRecurringIntervalMatchingType = COUPON_RECURRING_INTERVAL_NONE_TYPE;
			fullRedirectUrl = String.format("%s/subscriptions?recurringInterval=YEAR", redirectUrl);
		}

		MailMessageContext mailMessageContext = createCouponEmailContext(
			emailTitle, emailContentInlineImageUrl, code, couponLicenseGradeMatchingType,
			couponRecurringIntervalMatchingType, expiredDate, benefitOption, benefitValue, receiverEmail,
			fullRedirectUrl
		);

		emailService.send(mailMessageContext);
	}

	private MailMessageContext createCouponEmailContext(
		String emailTitle, String emailContentInlineImageUrl, String code,
		String couponLicenseGradeMatchingType,
		String couponRecurringIntervalMatchingType, ZonedDateTime expiredDate,
		CouponBenefitOption benefitOption, Long benefitValue,
		String receiverEmail, String fullRedirectUrl
	) {
		Context context = new Context();
		context.setVariable("emailContentInlineImageUrl", emailContentInlineImageUrl);
		context.setVariable("code", code);
		context.setVariable("couponLicenseGradeMatchingType", couponLicenseGradeMatchingType);
		context.setVariable("couponRecurringIntervalMatchingType", couponRecurringIntervalMatchingType);
		context.setVariable("expiredDate", dateFormat(expiredDate));
		context.setVariable("timeZoneInfo", getTimeZoneFormatString());
		context.setVariable("benefitOptionAndValue", benefitOptionAndValueFormat(benefitOption, benefitValue));
		context.setVariable("fullRedirectUrl", fullRedirectUrl);

		return MailMessageContext.builder()
			.mailContext(context)
			.mailType(Mail.COUPON_CODE)
			.to(receiverEmail)
			.subjectArguments(new String[] {emailTitle})
			.build();
	}

	private String getTimeZoneFormatString() {
		ZoneId zoneId = ZoneId.of(SecurityUtil.getCurrentZoneId());
		ZoneOffset zoneOffset = zoneId.getRules().getStandardOffset(Instant.now());

		int totalSeconds = zoneOffset.getTotalSeconds();
		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;

		return String.format("UTC %s%02d:%02d", (hours >= 0 ? "+" : ""), hours, minutes);
	}

	private String dateFormat(ZonedDateTime zonedDateTime) {
		ZonedDateTime convertedZonedDateTime = ZonedDateTimeUtil.convertToCurrentZonedDateTime(zonedDateTime);
		return convertedZonedDateTime.format(DateTimeFormatter.ofPattern("dd MMM, yyyy").withLocale(Locale.ENGLISH));
	}

	private String priceFormat(BigDecimal amount) {
		DecimalFormat formatter = new DecimalFormat("###,###.##");
		formatter.setRoundingMode(RoundingMode.DOWN);
		return formatter.format(amount);
	}

	private String benefitOptionAndValueFormat(CouponBenefitOption benefitOption, Long benefitValue) {
		switch (benefitOption) {
			case MAXIMUM_WORKSPACE:
				return String.format("Upgrade %s Workspace", benefitValue);
			case MAXIMUM_GROUP:
				return String.format("Upgrade %s work group", benefitValue);
			case MAXIMUM_GROUP_USER:
				return String.format("Upgrade %s group member", benefitValue);
			case STORAGE_SIZE_PER_MB:
				return String.format("Upgrade %s MB storage", benefitValue);
			case MAXIMUM_PROJECT:
				return String.format("Upgrade %s projects", benefitValue);
			case MAXIMUM_PUBLISHING_PROJECT:
				return String.format("Upgrade %s publishing projects", benefitValue);
			case MAXIMUM_VIEW_PER_MONTH:
				return String.format("Upgrade %s views", benefitValue);
			case YEAR:
				return String.format("Provide %s years", benefitValue);
			case MONTH:
				return String.format("Provide %s months", benefitValue);
			default:
				throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}
}
