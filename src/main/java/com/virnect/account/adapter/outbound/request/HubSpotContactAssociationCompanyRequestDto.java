package com.virnect.account.adapter.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HubSpotContactAssociationCompanyRequestDto {
	@JsonProperty(value = "properties")
	private ContactRequestDto contactRequestDto;

	public HubSpotContactAssociationCompanyRequestDto(ContactRequestDto contactRequestDto) {
		this.contactRequestDto = contactRequestDto;
	}
}
