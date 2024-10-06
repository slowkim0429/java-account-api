package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.OrganizationLicenseAttribute;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@RequiredArgsConstructor
public class OrganizationLicenseAttributeRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "Organization License Attribute Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Organization License Id", example = "1000000000")
	private Long organizationLicenseId;

	@ApiModelProperty(value = "License Attribute Id", example = "1000000000")
	private Long licenseAttributeId;

	@ApiModelProperty(value = "License Attribute Type", example = "MAXIMUM_GROUP")
	private LicenseAttributeType licenseAttributeType;

	@ApiModelProperty(value = "Data Type", example = "NUMBER")
	private DataType dataType;

	@ApiModelProperty(value = "Data Value", example = "1000")
	private String dataValue;

	@ApiModelProperty(value = "Status", example = "USE")
	private UseStatus status;

	@ApiModelProperty(value = "Organization License Attribute 의 생성 일자", example = "2022-04-26 11:29:33")
	private String createdDate;

	@ApiModelProperty(value = "Organization License Attribute 의 수정 일자", example = "2022-04-26 11:29:33")
	private String updatedDate;

	@ApiModelProperty(value = "Organization License Attribute 의 생성 User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "OOrganization License Attribute 의 수정 User Id", example = "1000000000")
	private Long updatedBy;

	public OrganizationLicenseAttributeRevisionResponseDto(
		RevisionType revisionType, OrganizationLicenseAttribute organizationLicenseAttribute
	) {
		this.revisionType = revisionType;
		this.id = organizationLicenseAttribute.getId();
		this.organizationLicenseId = organizationLicenseAttribute.getOrganizationLicenseId();
		this.licenseAttributeId = organizationLicenseAttribute.getLicenseAttributeId();
		this.licenseAttributeType = organizationLicenseAttribute.getLicenseAttributeType();
		this.dataType = organizationLicenseAttribute.getDataType();
		this.dataValue = organizationLicenseAttribute.getDataValue();
		this.status = organizationLicenseAttribute.getStatus();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(
			organizationLicenseAttribute.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(
			organizationLicenseAttribute.getUpdatedDate());
		this.createdBy = organizationLicenseAttribute.getCreatedBy();
		this.updatedBy = organizationLicenseAttribute.getLastModifiedBy();
	}

	public static OrganizationLicenseAttributeRevisionResponseDto of(
		Byte representation, OrganizationLicenseAttribute organizationLicenseAttribute
	) {
		return new OrganizationLicenseAttributeRevisionResponseDto(
			RevisionType.valueOf(representation), organizationLicenseAttribute);
	}
}
