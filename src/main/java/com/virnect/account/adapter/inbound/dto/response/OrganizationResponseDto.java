package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.CustomerType;
import com.virnect.account.domain.enumclass.OrganizationStatus;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.security.AES256Util;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@RequiredArgsConstructor
public class OrganizationResponseDto {
	@ApiModelProperty(value = "account id", example = "1000000000")
	private Long id;
	@ApiModelProperty(value = "이름", example = "버넥트")
	private String name;
	@ApiModelProperty(value = "이메일", example = "user@virnect.com")
	private String email;
	@ApiModelProperty(value = "사업자등록번호", example = "1018216927")
	private String vatIdentificationNumber;
	@ApiModelProperty(value = "사용 지역 아이디", example = "1000000000")
	private Long localeId;
	@ApiModelProperty(value = "사용 지역 코드", example = "KR")
	private String localeCode;
	@ApiModelProperty(value = "사용 지역 이름", example = "Korea, Republic of")
	private String localeName;
	@ApiModelProperty(value = "주소", example = "10-15, Hangang-daero 7-gil, Yongsan-gu")
	private String address;
	@ApiModelProperty(value = "우편번호", example = "04379")
	private String postalCode;
	@ApiModelProperty(value = "도시", example = "Seoul")
	private String city;
	@ApiModelProperty(value = "State Code (ISO-3166-2. For US/CA locale)", example = "US-AL")
	private String stateCode;
	@ApiModelProperty(value = "State Name (For US/CA locale)", example = "Alabama")
	private String stateName;
	@ApiModelProperty(value = "Province Name (For exclude US/CA locale)", example = "gyeonggi-do")
	private String province;
	@ApiModelProperty(value = "구매자 유형", example = "[NATURAL_PERSON, LEGAL_PERSON]")
	private CustomerType customerType;
	@ApiModelProperty(value = "first name", example = "Justin")
	private String firstName;
	@ApiModelProperty(value = "last name", example = "Bieber")
	private String lastName;
	@ApiModelProperty(value = "phone number", example = "1012345678")
	private String phoneNumber;
	@ApiModelProperty(value = "corporate number", example = "639183878")
	private String corporateNumber;
	@ApiModelProperty(value = "country calling code", example = "82")
	private Integer countryCallingCode;
	@ApiModelProperty(value = "생성자", example = "1000000000")
	private Long createdBy;
	@ApiModelProperty(value = "등록 일자", example = "2022-01-03T10:15:30")
	private String createdDate;
	@ApiModelProperty(value = "수정자", example = "1000000000")
	private Long updatedBy;
	@ApiModelProperty(value = "수정 일자", example = "2022-01-03T11:15:30")
	private String updatedDate;
	@ApiModelProperty(value = "organization status", example = "APPROVED")
	private OrganizationStatus status;
	@ApiModelProperty(value = "hubspot company id", example = "1000000000")
	private Long hubspotCompanyId;

	@QueryProjection
	public OrganizationResponseDto(
		Long id, String name, String email, String vatIdentificationNumber, Long localeId, String localeCode,
		String localeName, String address, String postalCode, String city, String stateCode, String stateName,
		String province, CustomerType customerType, String firstName, String lastName, String phoneNumber,
		String corporateNumber, Integer countryCallingCode, Long createdBy, ZonedDateTime createdDate, Long updatedBy,
		ZonedDateTime updatedDate, OrganizationStatus status, Long hubspotCompanyId
	) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.vatIdentificationNumber = vatIdentificationNumber;
		this.localeId = localeId;
		this.localeCode = localeCode;
		this.localeName = localeName;
		this.address = address;
		this.postalCode = postalCode;
		this.city = city;
		this.stateCode = stateCode;
		this.stateName = stateName;
		this.province = province;
		this.customerType = customerType;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.corporateNumber = corporateNumber;
		this.countryCallingCode = countryCallingCode;
		this.createdBy = createdBy;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedBy = updatedBy;
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.status = status;
		this.hubspotCompanyId = hubspotCompanyId;
	}

	public static OrganizationResponseDto from(Organization organization) {
		return new OrganizationResponseDto(
			organization.getId(), organization.getName(), organization.getEmail(),
			AES256Util.decrypt(organization.getVatIdentificationNumber()), organization.getLocaleId(),
			organization.getLocaleCode(), organization.getLocaleName(), AES256Util.decrypt(organization.getAddress()),
			organization.getPostalCode(), organization.getCity(), organization.getStateCode(),
			organization.getStateName(), organization.getProvince(), organization.getCustomerType(),
			AES256Util.decrypt(organization.getFirstName()), AES256Util.decrypt(organization.getLastName()),
			AES256Util.decrypt(organization.getPhoneNumber()), organization.getCorporateNumber(),
			organization.getCountryCallingCode(), organization.getCreatedBy(), organization.getCreatedDate(),
			organization.getLastModifiedBy(), organization.getUpdatedDate(), organization.getStatus(),
			organization.getHubspotCompanyId()
		);
	}
}
