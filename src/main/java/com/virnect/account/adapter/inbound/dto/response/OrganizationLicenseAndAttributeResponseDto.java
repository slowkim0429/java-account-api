package com.virnect.account.adapter.inbound.dto.response;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.model.OrganizationLicense;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@RequiredArgsConstructor
public class OrganizationLicenseAndAttributeResponseDto {
	@ApiModelProperty(value = "organization license id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "organization id", example = "1000000000")
	private Long organizationId;

	@ApiModelProperty(value = "organization license 상태", example = "PROCESSING")
	private OrganizationLicenseStatus status;

	@ApiModelProperty(value = "organization license 시작일자", example = "2022-04-26 11:29:33")
	private String startAt;

	@ApiModelProperty(value = "organization license 만료일자", example = "2022-04-26 11:29:33")
	private String expiredAt;

	@ApiModelProperty(value = "organization license의 license id", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "organization license의 license 명칭", example = "Squars Free License")
	private String licenseName;

	@ApiModelProperty(value = "organization license의 license Grade id", example = "1000000000")
	private Long licenseGradeId;

	@ApiModelProperty(value = "organization license의 license Grade 이름", example = "Free+")
	private String licenseGradeName;

	@ApiModelProperty(value = "organization license의 license Grade 타입", example = "FREE_PLUS")
	private LicenseGradeType licenseGradeType;

	@ApiModelProperty(value = "organization license의 구매한 item id", example = "1000000000")
	private Long itemId;

	@ApiModelProperty(value = "organization license의 contract id", example = "1000000000")
	private Long contractId;

	@ApiModelProperty(value = "organization license의 생성 일자", example = "2022-04-26 11:29:33")
	private String createdDate;

	@ApiModelProperty(value = "organization license의 수정 일자", example = "2022-04-26 11:29:33")
	private String updatedDate;

	@ApiModelProperty(value = "license attributes")
	private List<OrganizationLicenseAttributeResponseDto> licenseAttributes;

	public OrganizationLicenseAndAttributeResponseDto(
		OrganizationLicense organizationLicense,
		List<OrganizationLicenseAttributeResponseDto> organizationLicenseAttributes
	) {
		this.id = organizationLicense.getId();
		this.organizationId = organizationLicense.getOrganizationId();
		this.status = organizationLicense.getStatus();
		this.startAt = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getStartedAt());
		this.expiredAt = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getExpiredAt());
		this.licenseId = organizationLicense.getLicenseId();
		this.licenseName = organizationLicense.getLicenseName();
		this.licenseGradeId = organizationLicense.getLicenseGradeId();
		this.licenseGradeName = organizationLicense.getLicenseGradeName();
		this.licenseGradeType = organizationLicense.getLicenseGradeType();
		this.itemId = organizationLicense.getItemId();
		this.contractId = organizationLicense.getContractId();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getUpdatedDate());
		this.licenseAttributes = organizationLicenseAttributes;
	}
}
