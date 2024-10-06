package com.virnect.account.adapter.outbound.request;

import feign.form.FormProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HubSpotTokenRefreshRequestDto {
	@FormProperty(value = "grant_type")
	private String grantType;
	@FormProperty(value = "client_id")
	private String clientId;
	@FormProperty(value = "client_secret")
	private String clientSecret;
	@FormProperty(value = "redirect_uri")
	private String redirectUri;
	@FormProperty(value = "refresh_token")
	private String refreshToken;

	@Builder
	public HubSpotTokenRefreshRequestDto(
		String grantType, String clientId, String clientSecret, String redirectUri, String refreshToken
	) {
		this.grantType = grantType;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
		this.refreshToken = refreshToken;
	}
}
