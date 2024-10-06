package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
public class OrganizationLicenseKeyResponseDto {
	@ApiModelProperty(value = "organization license key id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "organization id", example = "1000000000")
	private Long organizationId;

	@ApiModelProperty(value = "organization license id", example = "1000000000")
	private Long organizationLicenseId;

	@ApiModelProperty(value = "email", example = "test@virnect.com")
	private String email;

	@ApiModelProperty(value = "license key")
	private String licenseKey;

	@ApiModelProperty(value = "status", example = "USE")
	private UseStatus useStatus;

	@ApiModelProperty(value = "Organization License key의 생성 일자", example = "2022-04-26 11:29:33")
	private String createdDate;

	@ApiModelProperty(value = "Organization License key의 수정 일자", example = "2022-04-26 11:29:33")
	private String updatedDate;

	@ApiModelProperty(value = "Organization License key의 생성 User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "Organization License key의 수정 User Id", example = "1000000000")
	private Long updatedBy;

	@QueryProjection
	public OrganizationLicenseKeyResponseDto(
		Long id, Long organizationId, Long organizationLicenseId, String email, String licenseKey,
		UseStatus useStatus, ZonedDateTime createdDate, ZonedDateTime updatedDate, Long createdBy, Long updatedBy
	) {
		this.id = id;
		this.organizationId = organizationId;
		this.organizationLicenseId = organizationLicenseId;
		this.email = email;
		this.licenseKey = licenseKey;
		this.useStatus = useStatus;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}
}
