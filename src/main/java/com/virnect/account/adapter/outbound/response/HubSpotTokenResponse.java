package com.virnect.account.adapter.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HubSpotTokenResponse {
	@JsonProperty(value = "access_token")
	private String accessToken;
	@JsonProperty(value = "token_type")
	private String tokenType;
	@JsonProperty(value = "expires_in")
	private Long expiresIn;
}
