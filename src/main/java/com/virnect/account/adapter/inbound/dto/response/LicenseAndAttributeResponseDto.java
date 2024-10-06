package com.virnect.account.adapter.inbound.dto.response;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.UseStatus;

@Getter
@ApiModel
@NoArgsConstructor
public class LicenseAndAttributeResponseDto {
	@ApiModelProperty(value = "license id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "product id", example = "1000000000")
	private Long productId;

	@ApiModelProperty(value = "product name", example = "SQUARS PRODUCT")
	private String productName;

	@ApiModelProperty(value = "product type", example = "SQUARS")
	private ProductType productType;

	@ApiModelProperty(value = "product status", example = "USE")
	private ApprovalStatus productStatus;

	@ApiModelProperty(value = "license grade id", example = "1000000000")
	private Long licenseGradeId;

	@ApiModelProperty(value = "license grade name", example = "Free")
	private String licenseGradeName;

	@ApiModelProperty(value = "license grade type", example = "FREE_PLUS")
	private LicenseGradeType licenseGradeType;

	@ApiModelProperty(value = "license grade status", example = "USE")
	private ApprovalStatus licenseGradeStatus;

	@ApiModelProperty(value = "license name", example = "squars license")
	private String name;

	@ApiModelProperty(value = "license description", example = "squars pro type license")
	private String description;

	@ApiModelProperty(value = "license status", example = "APPROVED")
	private ApprovalStatus status;

	@ApiModelProperty(value = "license use status", example = "USE")
	private UseStatus useStatus;

	@ApiModelProperty(value = "license attributes")
	private List<LicenseAttributeResponseDto> licenseAttributes;

	@ApiModelProperty(value = "license additional attributes")
	private List<LicenseAdditionalAttributeResponseDto> licenseAdditionalAttributes;

	@QueryProjection
	public LicenseAndAttributeResponseDto(
		Long id,
		Long productId,
		String productName,
		ProductType productType,
		ApprovalStatus productStatus,
		Long licenseGradeId,
		String licenseGradeName,
		LicenseGradeType licenseGradeType,
		ApprovalStatus licenseGradeStatus,
		String name,
		String description,
		ApprovalStatus status,
		UseStatus useStatus
	) {
		this.id = id;
		this.productId = productId;
		this.productName = productName;
		this.productType = productType;
		this.productStatus = productStatus;
		this.licenseGradeId = licenseGradeId;
		this.licenseGradeName = licenseGradeName;
		this.licenseGradeType = licenseGradeType;
		this.licenseGradeStatus = licenseGradeStatus;
		this.name = name;
		this.description = description;
		this.status = status;
		this.useStatus = useStatus;
	}

	public void setLicenseAttributes(
		List<LicenseAttributeResponseDto> licenseAttributes
	) {
		this.licenseAttributes = licenseAttributes;
	}

	public void setLicenseAdditionalAttributes(
		List<LicenseAdditionalAttributeResponseDto> licenseAdditionalAttributes
	) {
		this.licenseAdditionalAttributes = licenseAdditionalAttributes;
	}

}
