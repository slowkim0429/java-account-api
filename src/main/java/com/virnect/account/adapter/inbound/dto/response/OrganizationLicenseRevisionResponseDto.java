package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.model.OrganizationLicense;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@RequiredArgsConstructor
public class OrganizationLicenseRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

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

	@ApiModelProperty(value = "License Id", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "License Name", example = "License Name")
	private String licenseName;

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

	public OrganizationLicenseRevisionResponseDto(
		RevisionType revisionType, OrganizationLicense organizationLicense
	) {
		this.revisionType = revisionType;
		this.id = organizationLicense.getId();
		this.organizationId = organizationLicense.getOrganizationId();
		this.contractId = organizationLicense.getContractId();
		this.itemId = organizationLicense.getItemId();
		this.productId = organizationLicense.getProductId();
		this.productName = organizationLicense.getProductName();
		this.productType = organizationLicense.getProductType();
		this.licenseGradeId = organizationLicense.getLicenseGradeId();
		this.licenseGradeName = organizationLicense.getLicenseGradeName();
		this.licenseGradeType = organizationLicense.getLicenseGradeType();
		this.licenseId = organizationLicense.getLicenseId();
		this.licenseName = organizationLicense.getLicenseName();
		this.status = organizationLicense.getStatus();
		this.startedAt = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getStartedAt());
		this.expiredAt = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getExpiredAt());
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getUpdatedDate());
		this.createdBy = organizationLicense.getCreatedBy();
		this.updatedBy = organizationLicense.getLastModifiedBy();
	}

	public static OrganizationLicenseRevisionResponseDto of(
		Byte representation, OrganizationLicense organizationLicense
	) {
		return new OrganizationLicenseRevisionResponseDto(RevisionType.valueOf(representation), organizationLicense);
	}
}
