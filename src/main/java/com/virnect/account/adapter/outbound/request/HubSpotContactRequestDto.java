package com.virnect.account.adapter.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HubSpotContactRequestDto {
	@JsonProperty(value = "properties")
	private ContactRequestDto contactRequestDto;

	public HubSpotContactRequestDto(ContactRequestDto contactRequestDto) {
		this.contactRequestDto = contactRequestDto;
	}

	@Override
	public String toString() {
		return "HubSpotContactRequestDto{" +
			"contactRequestDto=" + contactRequestDto +
			'}';
	}
}
