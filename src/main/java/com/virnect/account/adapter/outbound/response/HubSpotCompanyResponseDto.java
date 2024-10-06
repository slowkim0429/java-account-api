package com.virnect.account.adapter.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HubSpotCompanyResponseDto {
	@JsonProperty(value = "id")
	private Long hubspotCompanyId;

	public static HubSpotCompanyResponseDto mock() {
		HubSpotCompanyResponseDto hubSpotCompanyResponseDto = new HubSpotCompanyResponseDto();
		hubSpotCompanyResponseDto.setHubspotCompanyId(null);
		return hubSpotCompanyResponseDto;
	}
}
