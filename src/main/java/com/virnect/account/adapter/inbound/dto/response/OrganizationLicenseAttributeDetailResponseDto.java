package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@NoArgsConstructor
@ApiModel
public class OrganizationLicenseAttributeDetailResponseDto {
	@ApiModelProperty(value = "Organization License Attribute Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Organization License Id", example = "1000000000")
	private Long organizationLicenseId;

	@ApiModelProperty(value = "License Attribute Id", example = "1000000000")
	private Long licenseAttributeId;

	@ApiModelProperty(value = "License Attribute Type", example = "MAXIMUM_WORKSPACE")
	private LicenseAttributeType licenseAttributeType;

	@ApiModelProperty(value = "License Attribute Data Type", example = "NUMBER")
	private DataType dataType;

	@ApiModelProperty(value = "License Attribute Data Value", example = "1")
	private String dataValue;

	@ApiModelProperty(value = "Organization License Attribute Status", example = "USE")
	private UseStatus status;

	@ApiModelProperty(value = "Organization License Attribute Created Date", example = "2022-04-26 11:29:33")
	private String createdDate;

	@ApiModelProperty(value = "Organization License Attribute Updated Date", example = "2022-04-26 11:29:33")
	private String updatedDate;

	@ApiModelProperty(value = "Organization License Attribute Created User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "Organization License Attribute Updated User Id", example = "1000000000")
	private Long updatedBy;

	@QueryProjection
	public OrganizationLicenseAttributeDetailResponseDto(
		Long id, Long organizationLicenseId, Long licenseAttributeId,
		LicenseAttributeType licenseAttributeType, DataType dataType, String dataValue,
		UseStatus status, ZonedDateTime createdDate, ZonedDateTime updatedDate, Long createdBy, Long updatedBy
	) {
		this.id = id;
		this.organizationLicenseId = organizationLicenseId;
		this.licenseAttributeId = licenseAttributeId;
		this.licenseAttributeType = licenseAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
		this.status = status;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}
}
