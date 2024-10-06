package com.virnect.account.adapter.inbound.dto.request.organization;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganizationLicenseKeySearchDto {
	@ApiModelProperty(value = "Organization License Id", example = "1000000000")
	private Long organizationLicenseId;

	@ApiModelProperty(value = "Organization Id", example = "1000000000")
	private Long organizationId;
}
