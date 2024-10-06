package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@RequiredArgsConstructor
public class OrganizationLicenseResponseDto {
	@ApiModelProperty(value = "Organization License Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Organization Id", example = "1000000000")
	private Long organizationId;

	@ApiModelProperty(value = "Contract Id", example = "1000000000")
	private Long contractId;

	@ApiModelProperty(value = "Item Id", example = "1000000000")
	private Long itemId;

	@ApiModelProperty(value = "Product Id", example = "1000000000")
	private Long productId;

	@ApiModelProperty(value = "Product Name", example = "SQUARS Name")
	private String productName;

	@ApiModelProperty(value = "Product Type", example = "SQUARS")
	private ProductType productType;

	@ApiModelProperty(value = "License Grade Id", example = "1000000000")
	private Long licenseGradeId;

	@ApiModelProperty(value = "License Grade Name", example = "Standard")
	private String licenseGradeName;

	@ApiModelProperty(value = "License Grade Type", example = "STANDARD")
	private LicenseGradeType licenseGradeType;

	@ApiModelProperty(value = "License Grade Description", example = "Standard Description")
	private String licenseGradeDescription;

	@ApiModelProperty(value = "License Id", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "License Name", example = "License Name")
	private String licenseName;

	@ApiModelProperty(value = "License Description", example = "License Descripition")
	private String licenseDescription;

	@ApiModelProperty(value = "License Sales Target", example = "License Sales Target")
	private String licenseSalesTarget;

	@ApiModelProperty(value = "Organization License Status", example = "PROCESSING")
	private OrganizationLicenseStatus status;

	@ApiModelProperty(value = "Organization License Start Date", example = "2022-04-26 11:29:33")
	private String startedAt;

	@ApiModelProperty(value = "Organization License Expired Date", example = "2022-04-26 11:29:33")
	private String expiredAt;

	@ApiModelProperty(value = "Organization License의 생성 일자", example = "2022-04-26 11:29:33")
	private String createdDate;

	@ApiModelProperty(value = "Organization License의 수정 일자", example = "2022-04-26 11:29:33")
	private String updatedDate;

	@ApiModelProperty(value = "Organization License의 생성 User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "Organization License의 수정 User Id", example = "1000000000")
	private Long updatedBy;

	@QueryProjection
	public OrganizationLicenseResponseDto(
		Long id, Long organizationId, Long contractId, Long itemId, Long productId,
		String productName, ProductType productType, Long licenseGradeId, String licenseGradeName,
		LicenseGradeType licenseGradeType, Long licenseId, String licenseName, OrganizationLicenseStatus status,
		ZonedDateTime startedAt, ZonedDateTime expiredAt,
		ZonedDateTime createdDate, ZonedDateTime updatedDate, Long createdBy, Long updatedBy
	) {
		this.id = id;
		this.organizationId = organizationId;
		this.contractId = contractId;
		this.itemId = itemId;
		this.productId = productId;
		this.productName = productName;
		this.productType = productType;
		this.licenseGradeId = licenseGradeId;
		this.licenseGradeName = licenseGradeName;
		this.licenseGradeType = licenseGradeType;
		this.licenseId = licenseId;
		this.licenseName = licenseName;
		this.status = status;
		this.startedAt = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(startedAt);
		this.expiredAt = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(expiredAt);
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}

	@QueryProjection
	public OrganizationLicenseResponseDto(
		Long id, Long organizationId, Long contractId, Long itemId, Long productId, String productName,
		ProductType productType, Long licenseGradeId, String licenseGradeName, LicenseGradeType licenseGradeType,
		String licenseGradeDescription, Long licenseId, String licenseName, String licenseDescription,
		String licenseSalesTarget,
		OrganizationLicenseStatus status, ZonedDateTime startedAt, ZonedDateTime expiredAt, ZonedDateTime createdDate,
		ZonedDateTime updatedDate, Long createdBy, Long updatedBy
	) {
		this.id = id;
		this.organizationId = organizationId;
		this.contractId = contractId;
		this.itemId = itemId;
		this.productId = productId;
		this.productName = productName;
		this.productType = productType;
		this.licenseGradeId = licenseGradeId;
		this.licenseGradeName = licenseGradeName;
		this.licenseGradeType = licenseGradeType;
		this.licenseGradeDescription = licenseGradeDescription;
		this.licenseId = licenseId;
		this.licenseName = licenseName;
		this.licenseDescription = licenseDescription;
		this.licenseSalesTarget = licenseSalesTarget;
		this.status = status;
		this.startedAt = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(startedAt);
		this.expiredAt = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(expiredAt);
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}
}
