package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailVerificationResponseDto {
	@ApiModelProperty(value = "인증 코드의 인증 확인 유무", example = "true")
	private final boolean hasAuthenticate;
	@ApiModelProperty(value = "인증 세션 코드", notes = "회원가입 관련한 세션에 대한 세션 코드입니다.", position = 2, example = "f74f09cd2ff143d2b49d27eaa81bfe09")
	private final String emailAuthCode;

	@Builder
	public EmailVerificationResponseDto(boolean hasAuthenticate, String emailAuthCode) {
		this.hasAuthenticate = hasAuthenticate;
		this.emailAuthCode = emailAuthCode;
	}
}
