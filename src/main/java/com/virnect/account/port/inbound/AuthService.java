package com.virnect.account.port.inbound;

import com.virnect.account.adapter.inbound.dto.request.LoginRequestDto;
import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.TokenRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;

public interface AuthService {

	void signup(UserCreateRequestDto userCreateRequestDto);
	TokenResponseDto signIn(LoginRequestDto loginDto);

	void signOut();

	TokenResponseDto reissue(TokenRequestDto tokenRequestDto);

	void emailAuthCodeValidation(String email, String emailAuthCode);

	void resign(PasswordVerificationRequestDto passwordVerificationRequestDto);
}
