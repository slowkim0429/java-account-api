package com.virnect.account.adapter.inbound.dto.response.invite;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.InviteRole;
import com.virnect.account.domain.enumclass.InviteStatus;
import com.virnect.account.domain.enumclass.InviteType;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.model.Invite;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@RequiredArgsConstructor
public class InviteRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "invite id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "organization id", example = "1000000000")
	private Long organizationId;

	@ApiModelProperty(value = "workspace id", example = "1000000000")
	private Long workspaceId;

	@ApiModelProperty(value = "group id", example = "1000000000")
	private Long groupId;

	@ApiModelProperty(value = "workspace invite id", example = "1000000000")
	private Long workspaceInviteId;

	@ApiModelProperty(value = "초대받은 유저의 email", example = "workspace-user@virnect.com")
	private String email;

	@ApiModelProperty(value = "invite status", example = "PENDING")
	private InviteStatus inviteStatus;

	@ApiModelProperty(value = "invite type", example = "WORKSPACE")
	private InviteType inviteType;

	@ApiModelProperty(value = "invite role", example = "ROLE_WORKSPACE_USER")
	private InviteRole inviteRole;

	@ApiModelProperty(value = "초대 만료 날짜", example = "2022-01-10T01:24:42")
	private String expiredDate;

	@ApiModelProperty(value = "Created Date", example = "2022-01-10T01:24:42")
	private String createdDate;

	@ApiModelProperty(value = "Updated Date", example = "2022-01-10T01:24:42")
	private String updatedDate;

	@ApiModelProperty(value = "Created User Id", example = "2022-01-10T01:24:42")
	private Long createdBy;

	@ApiModelProperty(value = "Updated User Id", example = "2022-01-10T01:24:42")
	private Long updatedBy;

	private InviteRevisionResponseDto(
		RevisionType revisionType, Invite invite
	) {
		this.revisionType = revisionType;
		this.id = invite.getId();
		this.organizationId = invite.getOrganizationId();
		this.workspaceId = invite.getWorkspaceId();
		this.groupId = invite.getGroupId();
		this.workspaceInviteId = invite.getWorkspaceInviteId();
		this.email = invite.getEmail();
		this.inviteStatus = invite.getInviteStatus();
		this.inviteType = invite.getInviteType();
		this.inviteRole = invite.getInviteRole();
		this.expiredDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(invite.getExpireDate());
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(invite.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(invite.getUpdatedDate());
		this.createdBy = invite.getCreatedBy();
		this.updatedBy = invite.getLastModifiedBy();
	}

	public static InviteRevisionResponseDto of(Byte representation, Invite invite) {
		return new InviteRevisionResponseDto(RevisionType.valueOf(representation), invite);
	}
}
