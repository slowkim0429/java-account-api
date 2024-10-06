package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.CustomerType;
import com.virnect.account.domain.enumclass.OrganizationStatus;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.security.AES256Util;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
public class OrganizationRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "organization id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "수정 일자", example = "2022-12-15T14:30:10")
	private String updatedDate;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedByUserId;

	@ApiModelProperty(value = "organization name", example = "virnect")
	private String name;

	@ApiModelProperty(value = "이메일", example = "user@virnect.com")
	private String email;

	@ApiModelProperty(value = "사용 지역 코드", example = "KR")
	private String localeCode;

	@ApiModelProperty(value = "사용 지역 이름", example = "Korea, Republic of")
	private String localeName;

	@ApiModelProperty(value = "State Code (ISO-3166-2. For US/CA locale)", example = "US-AL")
	private String stateCode;

	@ApiModelProperty(value = "State Name (For US/CA locale)", example = "Alabama")
	private String stateName;

	@ApiModelProperty(value = "주소", example = "10-15, Hangang-daero 7-gil, Yongsan-gu")
	private String address;

	@ApiModelProperty(value = "도시", example = "Seoul")
	private String city;

	@ApiModelProperty(value = "시/도", example = "gyeonggi-do")
	private String province;

	@ApiModelProperty(value = "우편번호", example = "04379")
	private String postalCode;

	@ApiModelProperty(value = "사업자등록번호", example = "1018216927")
	private String vatIdentificationNumber;

	@ApiModelProperty(value = "organization status", example = "APPROVED")
	private OrganizationStatus status;

	@ApiModelProperty(value = "hubspot company id", example = "23456")
	private Long hubspotCompanyId;

	@ApiModelProperty(value = "구매자 유형", example = "[NATURAL_PERSON, LEGAL_PERSON]")
	private CustomerType customerType;

	public OrganizationRevisionResponseDto(
		RevisionType revisionType, Organization organization
	) {
		this.revisionType = revisionType;
		this.id = organization.getId();
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organization.getUpdatedDate());
		this.updatedByUserId = organization.getLastModifiedBy();
		this.name = organization.getName();
		this.email = organization.getEmail();
		this.localeCode = organization.getLocaleCode();
		this.localeName = organization.getLocaleName();
		this.stateCode = organization.getStateCode();
		this.stateName = organization.getStateName();
		this.address =
			organization.getIsEncrypted() ? AES256Util.decrypt(organization.getAddress()) : organization.getAddress();
		this.city = organization.getCity();
		this.province = organization.getProvince();
		this.postalCode = organization.getPostalCode();
		this.vatIdentificationNumber =
			organization.getIsEncrypted() ? AES256Util.decrypt(organization.getVatIdentificationNumber()) :
				organization.getVatIdentificationNumber();
		this.status = organization.getStatus();
		this.hubspotCompanyId = organization.getHubspotCompanyId();
		this.customerType = organization.getCustomerType();
	}

	public static OrganizationRevisionResponseDto of(Byte representation, Organization organization) {
		return new OrganizationRevisionResponseDto(RevisionType.valueOf(representation), organization);
	}
}

