package com.virnect.account.adapter.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.model.Organization;
import com.virnect.account.security.AES256Util;

@Getter
@NoArgsConstructor
public class CompanyUpdateRequestDto {
	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "payment_email")
	private String email;

	@JsonProperty(value = "address")
	private String address;

	@JsonProperty(value = "city")
	private String city;

	@JsonProperty(value = "state")
	private String province;

	@JsonProperty(value = "zip")
	private String postalCode;

	@JsonProperty(value = "country")
	private String localeCode;

	@JsonProperty(value = "vat_id")
	private String vatIdentificationNumber;

	private CompanyUpdateRequestDto(
		String name, String email, String address, String city, String province, String postalCode, String localeCode,
		String vatIdentificationNumber
	) {
		this.name = name;
		this.email = email;
		this.address = address;
		this.city = city;
		this.province = province;
		this.postalCode = postalCode;
		this.localeCode = localeCode;
		this.vatIdentificationNumber = vatIdentificationNumber;
	}

	public static CompanyUpdateRequestDto from(Organization organization) {
		return new CompanyUpdateRequestDto(
			organization.getName(),
			organization.getEmail(),
			organization.getIsEncrypted() ? AES256Util.decrypt(organization.getAddress()) : organization.getAddress(),
			organization.getCity(),
			organization.getProvince(),
			organization.getPostalCode(),
			organization.getLocaleCode(),
			organization.getIsEncrypted() ? AES256Util.decrypt(organization.getVatIdentificationNumber()) :
				organization.getVatIdentificationNumber()
		);
	}

	@Override
	public String toString() {
		return "CompanyUpdateRequestDto{" +
			"name='" + name + '\'' +
			", email='" + email + '\'' +
			", address='" + address + '\'' +
			", city='" + city + '\'' +
			", province='" + province + '\'' +
			", postalCode='" + postalCode + '\'' +
			", localeCode='" + localeCode + '\'' +
			", vatIdentificationNumber='" + vatIdentificationNumber + '\'' +
			'}';
	}
}
