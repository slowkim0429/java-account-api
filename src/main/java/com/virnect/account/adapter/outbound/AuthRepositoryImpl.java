package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QUser.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.model.User;
import com.virnect.account.log.NoLogging;
import com.virnect.account.port.outbound.AuthRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	@NoLogging
	public Optional<User> getUserBySignIn(String email) {
		return Optional.ofNullable(
			queryFactory.selectFrom(user)
				.where(
					user.email.eq(email),
					user.status.eq(MembershipStatus.JOIN)
				)
				.fetchOne());
	}
}
