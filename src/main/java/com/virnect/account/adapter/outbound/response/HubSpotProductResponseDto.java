package com.virnect.account.adapter.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HubSpotProductResponseDto {
	@JsonProperty(value = "id")
	private Long hubSpotProductId;

	public static HubSpotProductResponseDto mock() {
		HubSpotProductResponseDto hubSpotProductResponseDto = new HubSpotProductResponseDto();
		hubSpotProductResponseDto.setHubSpotProductId(null);
		return hubSpotProductResponseDto;
	}
}
