package com.virnect.account.adapter.inbound.dto.response.invite;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.InviteRole;
import com.virnect.account.domain.enumclass.InviteStatus;
import com.virnect.account.domain.enumclass.InviteType;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@RequiredArgsConstructor
public class InviteResponseDto {
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

	@ApiModelProperty(value = "초대 날짜", example = "2022-01-10T01:24:42")
	private String invitedDate;

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

	@QueryProjection
	public InviteResponseDto(
		Long id, Long organizationId, Long workspaceId, String email, InviteStatus inviteStatus,
		InviteType inviteType, InviteRole inviteRole, ZonedDateTime invitedDate, ZonedDateTime expiredDate
	) {
		this.id = id;
		this.organizationId = organizationId;
		this.workspaceId = workspaceId;
		this.email = email;
		this.inviteStatus = inviteStatus;
		this.inviteType = inviteType;
		this.inviteRole = inviteRole;
		this.invitedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(invitedDate);
		this.expiredDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(expiredDate);
	}

	@QueryProjection
	public InviteResponseDto(
		Long id, Long organizationId, Long workspaceId, Long groupId, Long workspaceInviteId, String email,
		InviteStatus inviteStatus, InviteType inviteType, InviteRole inviteRole, ZonedDateTime invitedDate,
		ZonedDateTime expiredDate, ZonedDateTime createdDate, ZonedDateTime updatedDate, Long createdBy, Long updatedBy
	) {
		this.id = id;
		this.organizationId = organizationId;
		this.workspaceId = workspaceId;
		this.groupId = groupId;
		this.workspaceInviteId = workspaceInviteId;
		this.email = email;
		this.inviteStatus = inviteStatus;
		this.inviteType = inviteType;
		this.inviteRole = inviteRole;
		this.invitedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(invitedDate);
		this.expiredDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(expiredDate);
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}

	public void expired() {
		this.inviteStatus = InviteStatus.EXPIRED;
	}
}
