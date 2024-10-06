package com.virnect.account.domain.model;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.invite.WorkspaceInviteRequestDto;
import com.virnect.account.domain.enumclass.InviteRole;
import com.virnect.account.domain.enumclass.InviteStatus;
import com.virnect.account.domain.enumclass.InviteType;
import com.virnect.account.util.ZonedDateTimeUtil;

@Entity
@Getter
@Audited
@Table(name = "invite",
	indexes = {
		@Index(name = "idx_workspaceId", columnList = "workspace_id"),
		@Index(name = "idx_group_id", columnList = "group_id"),
		@Index(name = "idx_invite_type", columnList = "invite_status"),
		@Index(name = "idx_invite_status", columnList = "invite_status"),
		@Index(name = "idx_email", columnList = "email"),
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invite extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "email", nullable = false)
	@Size(max = 100)
	private String email;

	@Column(name = "expired_at", nullable = false)
	private ZonedDateTime expireDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "invite_status", nullable = false, length = 10)
	private InviteStatus inviteStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "invite_type", nullable = false, length = 20)
	private InviteType inviteType;

	@Enumerated(EnumType.STRING)
	@Column(name = "invite_role", nullable = false, length = 20)
	private InviteRole inviteRole;

	@Column(name = "organization_id", nullable = false)
	private Long organizationId;

	@Column(name = "workspace_id", nullable = false)
	private Long workspaceId;

	@Column(name = "group_id", nullable = true)
	private Long groupId;

	@Column(name = "workspace_invite_id", nullable = true)
	private Long workspaceInviteId;

	private Invite(
		String email, ZonedDateTime expireDate, Long organizationId, Long groupId, InviteRole inviteRole,
		InviteType inviteType,
		Long workspaceId, Long workspaceInviteId
	) {

		this.email = email;
		this.expireDate = expireDate;
		this.organizationId = organizationId;
		this.groupId = groupId;
		this.inviteRole = inviteRole;
		this.inviteType = inviteType;
		this.inviteStatus = InviteStatus.PENDING;
		this.workspaceId = workspaceId;
		this.workspaceInviteId = workspaceInviteId;
	}

	public static Invite create(
		String receiverEmail, ZonedDateTime expireDate, Long organizationId, Long workspaceId, Long groupId,
		InviteRole inviteRole,
		InviteType inviteType, Long workspaceInviteId
	) {
		return new Invite(
			receiverEmail, expireDate, organizationId, groupId, inviteRole, inviteType, workspaceId, workspaceInviteId);
	}

	public static Invite from(
		WorkspaceInviteRequestDto workspaceInviteRequestDto
	) {
		return new Invite(
			workspaceInviteRequestDto.getEmail(), workspaceInviteRequestDto.getExpiredDate(),
			workspaceInviteRequestDto.getOrganizationId()
			, null, workspaceInviteRequestDto.getInviteRole(), workspaceInviteRequestDto.getInviteType(),
			workspaceInviteRequestDto.getWorkspaceId(), workspaceInviteRequestDto.getWorkspaceInviteId()
		);
	}

	public void cancel() {
		this.inviteStatus = InviteStatus.CANCEL;
	}

	public void expired() {
		this.inviteStatus = InviteStatus.EXPIRED;
	}

	public void done() {
		this.inviteStatus = InviteStatus.DONE;
	}

	public boolean isExpired() {
		return this.expireDate.isBefore(ZonedDateTimeUtil.zoneOffsetOfUTC());
	}

	public void setInviteStatus(InviteStatus inviteStatus) {
		this.inviteStatus = inviteStatus;
	}
}
