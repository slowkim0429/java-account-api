package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.enumclass.UseStatus.*;
import static com.virnect.account.domain.model.QUser.*;
import static com.virnect.account.domain.model.QUserRole.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.model.UserRole;
import com.virnect.account.port.outbound.UserRoleRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqUserId(Long userId) {
		if (userId == null || userId <= 0) {
			return null;
		}
		return userRole.userId.eq(userId);
	}

	private BooleanExpression eqOrganizationId(Long organizationId) {
		if (organizationId == null || organizationId <= 0) {
			return null;
		}
		return user.organizationId.eq(organizationId);
	}

	private BooleanExpression eqRole(Role role) {
		if (role == null) {
			return null;
		}
		return userRole.role.eq(role);
	}

	@Override
	public List<UserRole> getUserUseRoles(Long userId) {
		return queryFactory.selectFrom(userRole)
			.where(
				eqUserId(userId),
				userRole.status.eq(USE)
			).fetch();
	}

	@Override
	public Optional<UserRole> getUserRoleByUserIdAndRole(Long userId, Role role) {
		return Optional.ofNullable(
			queryFactory.selectFrom(userRole).where(eqUserId(userId), eqRole(role), userRole.status.eq(USE))
				.fetchOne());

	}
}
