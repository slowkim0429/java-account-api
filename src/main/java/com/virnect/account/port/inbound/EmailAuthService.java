package com.virnect.account.port.inbound;

import java.time.ZonedDateTime;

import com.virnect.account.adapter.inbound.dto.request.AuthCodeVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.EmailAuthRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.password.EmailChangePasswordRequestDto;
import com.virnect.account.domain.enumclass.CouponBenefitOption;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.User;

public interface EmailAuthService {
	void emailAuthentication(EmailAuthRequestDto emailAuthRequestDto);

	void emailAuthCodeVerification(AuthCodeVerificationRequestDto authCodeVerificationRequestDto);

	void sendSignupWelcomeEmail(User user);

	void sendChangePasswordAuthEmail(EmailChangePasswordRequestDto emailChangePasswordRequestDto);

	void emailAuthCodeVerification(UpdatePasswordRequestDto updatePasswordRequestDto);

	void sendItemPaymentLinkEmail(User user, Item item);

	void sendUserResignEmail(String nickname, String email);

	void sendGroupUserInviteEmail(
		String currentUserEmail, String currentUserNickname, String receiverEmail, String groupName,
		String profileColor, Long workspaceId, Long groupId, String inviteToken, String localeCode
	);

	void sendCouponEmail(
		String emailTitle, String emailContentInlineImageUrl, String code,
		String couponLicenseGradeMatchingType,
		String couponRecurringIntervalMatchingType, ZonedDateTime expiredDate,
		CouponBenefitOption benefitOption, Long benefitValue, String receiverEmail
	);

	void sendOrganizationLicenseKeyEmail(String email, String licenseKey);
}
