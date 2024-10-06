package com.virnect.account.adapter.inbound.dto.response;

import java.time.Duration;

import org.apache.commons.lang.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.adapter.outbound.response.HubSpotTokenResponse;

@Getter
@ApiModel
public class HubSpotAccessTokenResponseDto {
	@ApiModelProperty(value = "인증 토큰 타입", example = "Bearer")
	private final String grantType;
	@ApiModelProperty(value = "access 토큰", position = 1, example = "abcd.abcd.abcd")
	private final String accessToken;
	@ApiModelProperty(value = "access 토큰 만료 기간(epoch milliseconds)", position = 2, example = "1641362827431")
	private final Long expireIn;

	private HubSpotAccessTokenResponseDto(String grantType, String accessToken, Long expireIn) {
		this.grantType = grantType;
		this.accessToken = accessToken;
		this.expireIn = expireIn;
	}

	public static HubSpotAccessTokenResponseDto from(HubSpotTokenResponse hubSpotTokenResponse) {
		return new HubSpotAccessTokenResponseDto(
			hubSpotTokenResponse.getTokenType(),
			hubSpotTokenResponse.getAccessToken(),
			Duration.ofSeconds(hubSpotTokenResponse.getExpiresIn()).toMillis()
		);
	}

	public static HubSpotAccessTokenResponseDto mock() {
		return new HubSpotAccessTokenResponseDto(StringUtils.EMPTY, StringUtils.EMPTY, 0L);
	}

	public String getGrantType() {
		return grantType.equals("bearer") ? "Bearer" : grantType;
	}

	@Override
	public String toString() {
		return "HubSpotAccessTokenResponseDto{" +
			"grantType='" + grantType + '\'' +
			", accessToken='" + accessToken + '\'' +
			", expireIn=" + expireIn +
			'}';
	}
}
