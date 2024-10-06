package com.virnect.account.adapter.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HubSpotCompanyUpdateRequestDto {
	@JsonProperty(value = "properties")
	private CompanyUpdateRequestDto companyUpdateRequestDto;

	public HubSpotCompanyUpdateRequestDto(CompanyUpdateRequestDto companyUpdateRequestDto) {
		this.companyUpdateRequestDto = companyUpdateRequestDto;
	}

	@Override
	public String toString() {
		return "HubSpotCompanyUpdateRequestDto{" +
			"companyUpdateRequestDto=" + companyUpdateRequestDto +
			'}';
	}
}
