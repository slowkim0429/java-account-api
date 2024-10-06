package com.virnect.account.adapter.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HubSpotContactUpdateRequestDto {
	@JsonProperty(value = "properties")
	private ContactUpdateRequestDto contactUpdateRequestDto;

	public HubSpotContactUpdateRequestDto(ContactUpdateRequestDto contactUpdateRequestDto) {
		this.contactUpdateRequestDto = contactUpdateRequestDto;
	}

	@Override
	public String toString() {
		return "HubSpotContactUpdateRequestDto{" +
			"contactUpdateRequestDto=" + contactUpdateRequestDto +
			'}';
	}
}
