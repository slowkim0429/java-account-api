package com.virnect.account.adapter.inbound.dto.request.invite;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.InviteStatus;
import com.virnect.account.domain.enumclass.InviteType;

@Getter
@Setter
@ApiModel
public class InviteSearchDto {
	@ApiModelProperty(value = "Workspace Id", example = "1000000000")
	private Long workspaceId;

	@ApiModelProperty(value = "Group Id", example = "1000000000")
	private Long groupId;

	@ApiModelProperty(value = "Email", example = "user@virnect.com")
	private String email;

	@ApiModelProperty(value = "Invite Type", example = "WORKSPACE")
	private InviteType inviteType;

	@ApiModelProperty(value = "Invite Status", example = "PENDING")
	private InviteStatus inviteStatus;
}
