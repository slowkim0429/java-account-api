package com.virnect.account.adapter.inbound.dto.request.adminuser;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class AdminUserAuthorityGroupRequestDto {

	@ApiModelProperty(value = "권한 그룹 ID", required = true, example = "10000000000")
	@Min(10000000000L)
	@NotNull
	private Long authorityGroupId;
}
