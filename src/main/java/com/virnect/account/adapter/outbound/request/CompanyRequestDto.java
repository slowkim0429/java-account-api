package com.virnect.account.adapter.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompanyRequestDto {
	@JsonProperty(value = "payment_email")
	private String email;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "country")
	private String localeCode;

	@JsonProperty(value = "type")
	private String type = "CUSTOMER";

	@Builder
	public CompanyRequestDto(String email, String name, String localeCode) {
		this.email = email;
		this.name = name;
		this.localeCode = localeCode;
	}

	@Override
	public String toString() {
		return "CompanyRequestDto{" +
			"email='" + email + '\'' +
			", name='" + name + '\'' +
			", localeCode='" + localeCode + '\'' +
			", type='" + type + '\'' +
			'}';
	}
}
