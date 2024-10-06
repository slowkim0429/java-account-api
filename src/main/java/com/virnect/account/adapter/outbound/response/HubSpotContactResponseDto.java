package com.virnect.account.adapter.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HubSpotContactResponseDto {
	@JsonProperty(value = "id")
	private Long hubspotContactId;

	public static HubSpotContactResponseDto mock() {
		HubSpotContactResponseDto hubSpotContactResponseDto = new HubSpotContactResponseDto();
		hubSpotContactResponseDto.setHubspotContactId(null);
		return hubSpotContactResponseDto;
	}
}
