package com.virnect.account.adapter.inbound.dto.request.organization;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganizationLicenseTrackSdkUsageSearchDto {
	@ApiModelProperty(value = "Organization License Key Id", example = "1000000000")
	private Long organizationLicenseKeyId;
}
