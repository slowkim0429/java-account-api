package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.enumclass.UseStatus.*;
import static com.virnect.account.domain.model.QAdminUser.*;
import static com.virnect.account.domain.model.QAdminUserRole.*;
import static com.virnect.account.domain.model.QAuthorityGroup.*;

import java.util.List;
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

import com.virnect.account.adapter.inbound.dto.request.user.AdminUserSearchDto;
import com.virnect.account.adapter.inbound.dto.response.AdminUserResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QAdminUserResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.model.AdminUser;
import com.virnect.account.port.outbound.AdminUserRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class AdminUserRepositoryImpl implements AdminUserRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqApprovalStatus(ApprovalStatus approvalStatus) {
		if (approvalStatus == null) {
			return null;
		}
		return adminUser.approvalStatus.eq(approvalStatus);
	}

	private BooleanExpression eqStatus(MembershipStatus status) {
		if (status == null) {
			return null;
		}
		return adminUser.status.eq(status);
	}

	private BooleanExpression eqId(Long id) {
		if (id == null || id == 0) {
			return null;
		}
		return adminUser.id.eq(id);
	}

	private BooleanExpression eqAuthorityGroupId(Long authorityGroupId) {
		if (authorityGroupId == null || authorityGroupId == 0) {
			return null;
		}
		return authorityGroup.id.eq(authorityGroupId);
	}

	private BooleanExpression startsWithEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return null;
		}
		return adminUser.email.startsWith(email);
	}

	@Override
	public Optional<AdminUser> getAdminUser(String email) {
		return Optional.ofNullable(
			queryFactory.selectFrom(adminUser)
				.where(
					adminUser.email.eq(email),
					adminUser.status.eq(MembershipStatus.JOIN)
				)
				.fetchOne());
	}

	@Override
	public Page<AdminUserResponseDto> getAdminUserResponses(AdminUserSearchDto adminUserSearchDto, Pageable pageable) {
		QueryResults<AdminUserResponseDto> pagingResult = queryFactory
			.select(new QAdminUserResponseDto(
					adminUser.id
					, adminUser.nickname
					, adminUser.email
					, adminUser.profileImage
					, adminUser.profileColor
					, adminUser.localeId
					, adminUser.localeCode
					, adminUser.regionId
					, adminUser.regionCode
					, adminUser.regionAwsCode
					, adminUser.language
					, adminUser.approvalStatus
					, adminUser.status
					, authorityGroup.name
					, adminUser.createdDate
					, adminUser.updatedDate
					, adminUser.createdBy
					, adminUser.lastModifiedBy
				)
			)
			.from(adminUser)
			.leftJoin(authorityGroup).on(authorityGroup.id.eq(adminUser.authorityGroupId))
			.where(
				eqApprovalStatus(adminUserSearchDto.getApprovalStatus())
				, eqAuthorityGroupId(adminUserSearchDto.getAuthorityGroupId())
				, eqStatus(adminUserSearchDto.getStatus())
				, eqId(adminUserSearchDto.getAdminUserId())
				, startsWithEmail(adminUserSearchDto.getEmail())
			)
			.orderBy(adminUser.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}

	@Override
	public List<AdminUser> getAdminUsersByRole(Role role) {
		return queryFactory
			.selectFrom(adminUser)
			.join(adminUserRole).on(adminUser.id.eq(adminUserRole.adminUserId))
			.where(
				adminUserRole.role.eq(role),
				adminUserRole.status.eq(USE),
				adminUser.status.eq(MembershipStatus.JOIN)
			)
			.fetch();
	}

	@Override
	public Optional<AdminUserResponseDto> getAdminUserResponse(Long userId) {
		return Optional.ofNullable(
			queryFactory
				.select(new QAdminUserResponseDto(
						adminUser.id
						, adminUser.nickname
						, adminUser.email
						, adminUser.profileImage
						, adminUser.profileColor
						, adminUser.localeId
						, adminUser.localeCode
						, adminUser.regionId
						, adminUser.regionCode
						, adminUser.regionAwsCode
						, adminUser.language
						, adminUser.approvalStatus
						, adminUser.status
						, authorityGroup.name
						, adminUser.createdDate
						, adminUser.updatedDate
						, adminUser.createdBy
						, adminUser.lastModifiedBy
					)
				)
				.from(adminUser)
				.leftJoin(authorityGroup).on(authorityGroup.id.eq(adminUser.authorityGroupId))
				.where(
					adminUser.id.eq(userId)
				)
				.fetchOne());

	}

	@Override
	public Optional<AdminUser> getAdminUser(Long adminUserId) {
		return Optional.ofNullable(
			queryFactory.selectFrom(adminUser)
				.where(adminUser.id.eq(adminUserId)
				)
				.fetchOne());
	}

	@Override
	public Optional<AdminUser> getAdminUser(
		MembershipStatus status, ApprovalStatus approvalStatus, Long authorityGroupId
	) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(adminUser)
				.where(
					adminUser.status.eq(status),
					adminUser.approvalStatus.eq(approvalStatus),
					adminUser.authorityGroupId.eq(authorityGroupId)
				)
				.fetchFirst()
		);
	}
}
