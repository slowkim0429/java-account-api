package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.log.NoLogging;

@Getter
@NoLogging
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenResponseDto {
	@ApiModelProperty(value = "인증 토큰 타입", example = "Bearer")
	private String grantType;
	@ApiModelProperty(value = "access 토큰", position = 1, example = "abcd.abcd.abcd")
	private String accessToken;
	@ApiModelProperty(value = "refresh 토큰", position = 2, example = "abcd.abcd.abcd")
	private String refreshToken;
	@ApiModelProperty(value = "access 토큰 만료 기간(epoch milliseconds)", position = 3, example = "1641362827431")
	private Long accessTokenExpiresIn;

	@Builder
	public TokenResponseDto(
		String grantType, String accessToken, String refreshToken, Long accessTokenExpiresIn
	) {
		this.grantType = grantType;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.accessTokenExpiresIn = accessTokenExpiresIn;
	}
}
