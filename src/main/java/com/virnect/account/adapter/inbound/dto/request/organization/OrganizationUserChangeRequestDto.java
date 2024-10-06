package com.virnect.account.adapter.inbound.dto.request.organization;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.domain.enumclass.UseStatus;

@Setter
@Getter
@NoArgsConstructor
public class OrganizationUserChangeRequestDto {
	@ApiModelProperty(value = "Account id", example = "1000000001", required = true)
	@NotNull(message = "organizationId 반드시 입력되어야 합니다.")
	private Long organizationId;

	@ApiModelProperty(value = "User id", example = "1000000011", required = true)
	@NotNull(message = "userId 반드시 입력되어야 합니다.")
	private Long userId;

	@ApiModelProperty(value = "사용여부", example = "[USE, UNUSE]", required = true)
	@NotNull(message = "Owner 여부 반드시 입력되어야 합니다.")
	private UseStatus ownerStatus;
}
