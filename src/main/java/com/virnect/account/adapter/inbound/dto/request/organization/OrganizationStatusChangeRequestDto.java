package com.virnect.account.adapter.inbound.dto.request.organization;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.domain.enumclass.OrganizationStatus;

@Setter
@Getter
@NoArgsConstructor
public class OrganizationStatusChangeRequestDto {
	@ApiModelProperty(value = "Account id", example = "1000000001", required = true)
	@NotNull(message = "organizationId 반드시 입력되어야 합니다.")
	private Long organizationId;

	@ApiModelProperty(value = "REGISTER,REVIEWING,PENDING,REJECT,APPROVED", example = "REGISTER", required = true)
	@NotNull(message = "변경할 Account 상태가 반드시 입력되어야 합니다.")
	private OrganizationStatus status;
}
