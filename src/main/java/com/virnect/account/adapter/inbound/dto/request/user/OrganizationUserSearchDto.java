package com.virnect.account.adapter.inbound.dto.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class OrganizationUserSearchDto {
	@ApiModelProperty(value = "organization master의 id", hidden = true)
	private Long organizationId;

	@ApiModelProperty(value = "검색 대상 사용자 이메일", position = 1)
	private String email;

	@ApiModelProperty(value = "검색 대상 사용자 닉네임", position = 2)
	private String nickname;

}
