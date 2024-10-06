package com.virnect.account.adapter.outbound.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.InviteRole;
import com.virnect.account.domain.model.User;

@Getter
@NoArgsConstructor
public class GroupUserByInviteRequestDto {
	private Long workspaceId;
	private Long groupId;
	private Long userId;
	private Long sendUserId;
	private InviteRole role;

	private GroupUserByInviteRequestDto(
		Long workspaceId, Long groupId, Long userId, Long sendUserId, InviteRole role
	) {
		this.workspaceId = workspaceId;
		this.groupId = groupId;
		this.userId = userId;
		this.sendUserId = sendUserId;
		this.role = role;
	}

	public static GroupUserByInviteRequestDto of(
		Long workspaceId, Long groupId, User user, Long sendUserId, InviteRole role
	) {
		return new GroupUserByInviteRequestDto(
			workspaceId, groupId, user.getId(), sendUserId, role
		);
	}
}
