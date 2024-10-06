package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
public class OrganizationLicenseTrackSdkUsageResponseDto {
	@ApiModelProperty(value = "Organization License Track Sdk Usage Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Organization License Key Id", example = "1000000000")
	private Long organizationLicenseKeyId;

	@ApiModelProperty(value = "Organization License Track Sdk Usage Content", example = "{}")
	private String content;

	@ApiModelProperty(value = "Organization License Track Sdk Usage 의 생성 일자", example = "2022-04-26 11:29:33")
	private String createdDate;

	@ApiModelProperty(value = "Organization License Track Sdk Usage 의 수정 일자", example = "2022-04-26 11:29:33")
	private String updatedDate;

	@ApiModelProperty(value = "Organization License Track Sdk Usage 의 생성 User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "OOrganization License Track Sdk Usage 의 수정 User Id", example = "1000000000")
	private Long updatedBy;

	@QueryProjection
	public OrganizationLicenseTrackSdkUsageResponseDto(
		Long id, Long organizationLicenseKeyId, String content, ZonedDateTime createdDate, ZonedDateTime updatedDate,
		Long createdBy,
		Long updatedBy
	) {
		this.id = id;
		this.organizationLicenseKeyId = organizationLicenseKeyId;
		this.content = content;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}
}
