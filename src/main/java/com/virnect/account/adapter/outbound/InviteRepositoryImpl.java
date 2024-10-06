package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QInvite.*;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.invite.InviteSearchDto;
import com.virnect.account.adapter.inbound.dto.response.invite.InviteResponseDto;
import com.virnect.account.adapter.inbound.dto.response.invite.QInviteResponseDto;
import com.virnect.account.domain.enumclass.InviteStatus;
import com.virnect.account.domain.enumclass.InviteType;
import com.virnect.account.domain.model.Invite;
import com.virnect.account.port.outbound.InviteRepositoryCustom;
import com.virnect.account.util.ZonedDateTimeUtil;

@Repository
@RequiredArgsConstructor
public class InviteRepositoryImpl implements InviteRepositoryCustom {
	private final JPAQueryFactory query;

	private BooleanExpression eqGroupId(Long groupId) {
		if (groupId == null || groupId <= 0) {
			return null;
		}
		return invite.groupId.eq(groupId);
	}

	private BooleanExpression eqWorkspaceId(Long workspaceId) {
		if (workspaceId == null || workspaceId <= 0) {
			return null;
		}
		return invite.workspaceId.eq(workspaceId);
	}

	private BooleanExpression eqInviteStatus(InviteStatus inviteStatus) {
		if (inviteStatus == null) {
			return null;
		}
		if (InviteStatus.EXPIRED.equals(inviteStatus)) {
			return invite.inviteStatus.eq(InviteStatus.PENDING)
				.and(invite.expireDate.before(ZonedDateTimeUtil.zoneOffsetOfUTC()));
		}

		if (InviteStatus.PENDING.equals(inviteStatus)) {
			return invite.inviteStatus.eq(InviteStatus.PENDING)
				.and(invite.expireDate.after(ZonedDateTimeUtil.zoneOffsetOfUTC()));
		}

		return invite.inviteStatus.eq(inviteStatus);
	}

	private BooleanExpression eqInviteType(InviteType inviteType) {
		if (inviteType == null) {
			return null;
		}
		return invite.inviteType.eq(inviteType);
	}

	private BooleanExpression startsWithEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return null;
		}
		return invite.email.startsWith(email);
	}

	@Override
	public Optional<Invite> getInvite(
		Long workspaceId, Long groupId, String receiverEmail, InviteStatus inviteStatus, InviteType inviteType
	) {
		return Optional.ofNullable(
			query.selectFrom(invite)
				.where(
					invite.workspaceId.eq(workspaceId),
					eqGroupId(groupId),
					invite.email.eq(receiverEmail),
					invite.inviteStatus.eq(inviteStatus),
					invite.inviteType.eq(inviteType)
				)
				.fetchOne()
		);
	}

	@Override
	public Optional<Invite> getInvite(Long id) {
		return Optional.ofNullable(
			query.selectFrom(invite)
				.where(
					invite.id.eq(id)
				)
				.fetchOne()
		);
	}

	@Override
	public Optional<InviteResponseDto> getInviteResponse(Long id) {
		return Optional.ofNullable(
			query.select(new QInviteResponseDto(
						invite.id,
						invite.organizationId,
						invite.workspaceId,
						invite.groupId,
						invite.workspaceInviteId,
						invite.email,
						invite.inviteStatus,
						invite.inviteType,
						invite.inviteRole,
						invite.createdDate,
						invite.expireDate,
						invite.createdDate,
						invite.updatedDate,
						invite.createdBy,
						invite.lastModifiedBy
					)
				)
				.from(invite)
				.where(
					invite.id.eq(id)
				)
				.fetchOne()
		);
	}

	@Override
	public Optional<Invite> getWorkspaceInvite(Long workspaceInviteId) {
		return Optional.ofNullable(
			query.selectFrom(invite)
				.where(
					invite.workspaceInviteId.eq(workspaceInviteId)
				)
				.fetchOne()
		);

	}

	@Override
	public Page<InviteResponseDto> getInviteResponses(
		Long organizationId, Long workspaceId, InviteType inviteType, Pageable pageable
	) {
		QueryResults<InviteResponseDto> pagingResult = query
			.select(new QInviteResponseDto(
					invite.id,
					invite.organizationId,
					invite.workspaceId,
					invite.email,
					invite.inviteStatus,
					invite.inviteType,
					invite.inviteRole,
					invite.createdDate,
					invite.expireDate
				)
			)
			.from(invite)
			.where(
				invite.organizationId.eq(organizationId),
				invite.workspaceId.eq(workspaceId),
				invite.inviteType.eq(inviteType)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(invite.id.desc())
			.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}

	@Override
	public Page<InviteResponseDto> getInviteResponses(InviteSearchDto inviteSearchDto, Pageable pageable) {
		QueryResults<InviteResponseDto> pagingResult = query
			.select(new QInviteResponseDto(
					invite.id,
					invite.organizationId,
					invite.workspaceId,
					invite.groupId,
					invite.workspaceInviteId,
					invite.email,
					invite.inviteStatus,
					invite.inviteType,
					invite.inviteRole,
					invite.createdDate,
					invite.expireDate,
					invite.createdDate,
					invite.updatedDate,
					invite.createdBy,
					invite.lastModifiedBy
				)
			)
			.from(invite)
			.where(
				eqWorkspaceId(inviteSearchDto.getWorkspaceId()),
				eqGroupId(inviteSearchDto.getGroupId()),
				eqInviteType(inviteSearchDto.getInviteType()),
				eqInviteStatus(inviteSearchDto.getInviteStatus()),
				startsWithEmail(inviteSearchDto.getEmail())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(invite.id.desc())
			.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}
}
