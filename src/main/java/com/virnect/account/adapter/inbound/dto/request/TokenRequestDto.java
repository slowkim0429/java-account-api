package com.virnect.account.adapter.inbound.dto.request;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.log.NoLogging;

@Getter
@NoLogging
@NoArgsConstructor
public class TokenRequestDto {
	@ApiModelProperty(value = "Access Token")
	private String accessToken;

	@ApiModelProperty(value = "Refresh Token")
	private String refreshToken;

	private TokenRequestDto(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public static TokenRequestDto of(String accessToken, String refreshToken) {
		return new TokenRequestDto(accessToken, refreshToken);
	}

	@ApiModelProperty(hidden = true)
	public boolean isValid() {
		return StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(refreshToken);
	}

	@ApiModelProperty(hidden = true)
	public String getInvalidMessage() {
		return "Access Token 과 Refresh Token 은 빈값이 아니어야 합니다.";
	}
}
