package com.virnect.account.adapter.inbound.dto.request.organization;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.enumclass.ProductType;

@Setter
@Getter
public class OrganizationLicenseSearchDto {
	@ApiModelProperty(value = "Organization License Id", example = "1000000000")
	private Long organizationLicenseId;

	@ApiModelProperty(value = "Organization Id", example = "1000000000")
	private Long organizationId;

	@ApiModelProperty(value = "Contract Id", example = "1000000000")
	private Long contractId;

	@ApiModelProperty(value = "Product Id", example = "1000000000")
	private Long productId;

	@ApiModelProperty(value = "License Grade Id", example = "1000000000")
	private Long licenseGradeId;

	@ApiModelProperty(value = "License Id", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "Organization License Status", example = "PROCESSING")
	private OrganizationLicenseStatus status;

	@ApiModelProperty(value = "Product Type", example = "SQUARS")
	private ProductType productType;
}
