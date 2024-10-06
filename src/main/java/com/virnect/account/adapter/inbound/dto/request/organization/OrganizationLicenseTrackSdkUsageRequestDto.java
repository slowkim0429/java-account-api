package com.virnect.account.adapter.inbound.dto.request.organization;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrganizationLicenseTrackSdkUsageRequestDto {
	@ApiModelProperty(value = "content", example = "track.dll", required = true)
	@NotBlank
	@Size(max = 100)
	private String content;
}
