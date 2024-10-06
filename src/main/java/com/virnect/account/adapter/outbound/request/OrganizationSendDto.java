package com.virnect.account.adapter.outbound.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.CustomerType;
import com.virnect.account.domain.enumclass.OrganizationStatus;
import com.virnect.account.domain.model.Organization;

@Getter
public class OrganizationSendDto {
	@ApiModelProperty(value = "이름", example = "virnect")
	private String name;

	@ApiModelProperty(value = "이메일", example = "virnect@virnect.com")
	private String email;

	@ApiModelProperty(value = "사업자등록번호", example = "123456789")
	private String vatIdentificationNumber;

	@ApiModelProperty(value = "지역 Id", example = "1000000208")
	private Long localeId;

	@ApiModelProperty(value = "지역 코드", example = "ES")
	private String localeCode;

	@ApiModelProperty(value = "지역 이름", example = "Korea, Republic of")
	private String localeName;

	@ApiModelProperty(value = "State Code", example = "CA-BC")
	private String stateCode;

	@ApiModelProperty(value = "State Name", example = "British Columbia")
	private String stateName;

	@ApiModelProperty(value = "주소", example = "10-15, Hangang-daero 7-gil, Yongsan-gu")
	private String address;

	@ApiModelProperty(value = "시/도", example = "Seoul")
	private String city;

	@ApiModelProperty(value = "구", example = "Yongsan-gu")
	private String province;

	@ApiModelProperty(value = "우편번호", example = "04379")
	private String postalCode;

	@ApiModelProperty(value = "구매자 유형", example = "[NATURAL_PERSON, LEGAL_PERSON]")
	private CustomerType customerType;

	@ApiModelProperty(value = "first name", example = "Justin")
	private String firstName;

	@ApiModelProperty(value = "last name", example = "Bieber")
	private String lastName;

	@ApiModelProperty(value = "phone number", example = "1012345678")
	private String phoneNumber;

	@ApiModelProperty(value = "corporate number", example = "23452345")
	private String corporateNumber;

	@ApiModelProperty(value = "country calling code", example = "82")
	private Integer countryCallingCode;

	@ApiModelProperty(value = "상태 값", example = "APPROVED")
	private OrganizationStatus status;

	@ApiModelProperty(value = "허브스팟 회사 정보", example = "567037")
	private Long hubspotCompanyId;

	@ApiModelProperty(value = "생성자 id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "수정자 id", example = "1000000000")
	private Long lastModifiedBy;

	private OrganizationSendDto(Organization organization) {
		this.name = organization.getName();
		this.email = organization.getEmail();
		this.vatIdentificationNumber = organization.getVatIdentificationNumber();
		this.localeId = organization.getLocaleId();
		this.localeCode = organization.getLocaleCode();
		this.localeName = organization.getLocaleName();
		this.stateCode = organization.getStateCode();
		this.stateName = organization.getStateName();
		this.address = organization.getAddress();
		this.city = organization.getCity();
		this.province = organization.getProvince();
		this.postalCode = organization.getPostalCode();
		this.status = organization.getStatus();
		this.hubspotCompanyId = organization.getHubspotCompanyId();
		this.createdBy = organization.getCreatedBy();
		this.lastModifiedBy = organization.getLastModifiedBy();
		this.customerType = organization.getCustomerType();
		this.firstName = organization.getFirstName();
		this.lastName = organization.getLastName();
		this.phoneNumber = organization.getPhoneNumber();
		this.corporateNumber = organization.getCorporateNumber();
		this.countryCallingCode = organization.getCountryCallingCode();
	}

	public static OrganizationSendDto from(Organization organization) {
		return new OrganizationSendDto(organization);
	}
}
