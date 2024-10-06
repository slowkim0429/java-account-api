package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.Role;

@ApiModel
@Getter
public class RoleResponseDto {
	@ApiModelProperty(value = "role 정보", position = 0, example = "ROLE_ORGANIZATION_OWNER")
	private final Role role;
	@ApiModelProperty(value = "정보 생성일자", position = 1, example = "2022-01-04T11:44:55")
	private final String createdDate;

	public RoleResponseDto(Role role, String createdDate) {
		this.role = role;
		this.createdDate = createdDate;
	}
}
