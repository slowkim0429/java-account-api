package com.virnect.account.adapter.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HubSpotCompanyRequestDto {
	@JsonProperty(value = "properties")
	private CompanyRequestDto companyRequestDto;

	public HubSpotCompanyRequestDto(CompanyRequestDto companyRequestDto) {
		this.companyRequestDto = companyRequestDto;
	}

	@Override
	public String toString() {
		return "HubSpotCompanyRequestDto{" +
			"companyRequestDto=" + companyRequestDto +
			'}';
	}
}
