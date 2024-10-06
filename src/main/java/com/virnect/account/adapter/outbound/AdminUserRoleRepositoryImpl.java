package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QAdminUserRole.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.AdminUserRole;
import com.virnect.account.port.outbound.AdminUserRoleRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class AdminUserRoleRepositoryImpl implements AdminUserRoleRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<AdminUserRole> getAdminUserRoles(Long adminUserId) {
		return queryFactory.selectFrom(adminUserRole)
			.where(
				adminUserRole.adminUserId.eq(adminUserId),
				adminUserRole.status.eq(UseStatus.USE)
			)
			.fetch();
	}

}
