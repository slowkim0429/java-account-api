package com.virnect.account.port.inbound;

import com.virnect.account.adapter.inbound.dto.request.TokenRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.AdminUserRequestDto;
import com.virnect.account.adapter.inbound.dto.request.validate.AdminUserLoginRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;

public interface AuthAdminService {
	void signUp(AdminUserRequestDto adminUserRequestDto);

	TokenResponseDto signIn(AdminUserLoginRequestDto adminUserLoginRequestDto);

	void signOut();

	void signOut(Long adminUserId);

	TokenResponseDto reissue(TokenRequestDto tokenRequestDto);
}
