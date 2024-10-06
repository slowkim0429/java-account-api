package com.virnect.account.adapter.outbound.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.domain.enumclass.InviteRole;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
public class WorkspaceUserCreateSendDto {
	private Long workspaceId;
	private Long userId;
	private InviteRole inviteSubRole;

	private WorkspaceUserCreateSendDto(
		Long workspaceId, Long userId, InviteRole inviteSubRole
	) {
		this.workspaceId = workspaceId;
		this.userId = userId;
		this.inviteSubRole = inviteSubRole;
	}

	public static WorkspaceUserCreateSendDto of(Long workspaceId, Long userId, InviteRole role) {
		return new WorkspaceUserCreateSendDto(workspaceId, userId, role);
	}
}
