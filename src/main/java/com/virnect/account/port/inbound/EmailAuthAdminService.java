package com.virnect.account.port.inbound;

import java.time.ZonedDateTime;

import com.virnect.account.adapter.inbound.dto.request.AuthCodeVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.password.EmailChangePasswordRequestDto;

public interface EmailAuthAdminService {

	void sendResetPasswordEmailAuthCode(EmailChangePasswordRequestDto emailChangePasswordRequestDto);

	void verifyResetPasswordEmailAuthCode(AuthCodeVerificationRequestDto authCodeVerificationRequestDto);

	void sendAdminAccountApplyEmail(
		Long applyUserRegionId, String applyUserEmail, String adminMasterUserEmail, String applyUserNickname
	);

	void sendAdminAccountApprovedEmail(
		Long approvedUserRegionId, String approvedUserEmail, String approvedUserNickname, ZonedDateTime approevdDate
	);
}
