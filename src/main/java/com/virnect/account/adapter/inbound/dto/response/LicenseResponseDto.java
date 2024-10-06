package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class LicenseResponseDto {
	@ApiModelProperty(value = "license id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "product id", example = "1000000000")
	private Long productId;

	@ApiModelProperty(value = "product type", example = "SQUARS")
	private ProductType productType;

	@ApiModelProperty(value = "product name", example = "squars product")
	private String productName;

	@ApiModelProperty(value = "license type id", example = "1000000000")
	private Long licenseGradeId;

	@ApiModelProperty(value = "license grade name")
	private String licenseGradeName;

	@ApiModelProperty(value = "license grade type", example = "Professional")
	private LicenseGradeType licenseGradeType;

	@ApiModelProperty(value = "license name", example = "squars license")
	private String name;

	@ApiModelProperty(value = "license description", example = "squars pro type license")
	private String description;

	@ApiModelProperty(value = "license status", example = "APPROVED")
	private ApprovalStatus status;

	@ApiModelProperty(value = "license use status", example = "USE")
	private UseStatus useStatus;

	@ApiModelProperty(value = "sales target", example = "For starter")
	private String salesTarget;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "등록일시", example = "2022-01-03T11:15:30")
	private String createdDate;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03T11:15:30")
	private String updatedDate;

	@QueryProjection
	public LicenseResponseDto(
		Long id, Long productId, ProductType productType, String productName, Long licenseGradeId,
		String licenseGradeName, LicenseGradeType licenseGradeType, String name, String description,
		ApprovalStatus status, UseStatus useStatus, String salesTarget, Long createdBy, ZonedDateTime createdDate,
		Long updatedBy, ZonedDateTime updatedDate
	) {
		this.id = id;
		this.productId = productId;
		this.productType = productType;
		this.productName = productName;
		this.licenseGradeId = licenseGradeId;
		this.licenseGradeName = licenseGradeName;
		this.licenseGradeType = licenseGradeType;
		this.name = name;
		this.description = description;
		this.status = status;
		this.useStatus = useStatus;
		this.salesTarget = salesTarget;
		this.createdBy = createdBy;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedBy = updatedBy;
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
	}
}
