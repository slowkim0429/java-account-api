package com.virnect.account.adapter.inbound.dto.request.invite;

import java.time.ZonedDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.InviteRole;
import com.virnect.account.domain.enumclass.InviteStatus;
import com.virnect.account.domain.enumclass.InviteType;

@Getter
@Setter
public class WorkspaceInviteRequestDto {
	@ApiModelProperty(value = "Workspace Invite Id", example = "1000000000", required = true)
	@Min(value = 1000000000)
	@NotNull
	private Long workspaceInviteId;

	@ApiModelProperty(value = "Email", example = "user@virnect.com", required = true)
	@NotBlank
	private String email;

	@ApiModelProperty(value = "Invite Type", example = "WORKSPACE", required = true)
	@NotNull
	private InviteType inviteType;

	@ApiModelProperty(value = "Invite Role", example = "ROLE_WORKSPACE_USER", required = true)
	@NotNull
	private InviteRole inviteRole;

	@ApiModelProperty(value = "Invite Status", example = "PENDING", required = true)
	@NotNull
	private InviteStatus inviteStatus;

	@ApiModelProperty(value = "Workspace Id", example = "1000000000", required = true)
	@NotNull
	@Min(value = 1000000000)
	private Long workspaceId;

	@ApiModelProperty(value = "Organization Id", example = "1000000000", required = true)
	@NotNull
	@Min(value = 1000000000)
	private Long organizationId;

	@ApiModelProperty(value = "Expired Date", example = "2023-08-11 05:44:13", required = true)
	@NotNull
	private ZonedDateTime expiredDate;
}
