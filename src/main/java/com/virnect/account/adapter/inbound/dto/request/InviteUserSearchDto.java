package com.virnect.account.adapter.inbound.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.InviteType;

@Getter
@Setter
@ApiModel
public class InviteUserSearchDto {
	@ApiModelProperty(value = "email 검색어")
	private String email;
	@ApiModelProperty(value = "초대 타입 검색어", example = "ACCOUNT_USER")
	private InviteType inviteType;
}
