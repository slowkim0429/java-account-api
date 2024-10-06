package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QLicense.*;
import static com.virnect.account.domain.model.QLicenseGrade.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.model.LicenseGrade;
import com.virnect.account.port.outbound.LicenseWithGradeRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class LicenseWithGradeRepositoryImpl implements LicenseWithGradeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<LicenseGrade> getLicenseGrade(Long licenseId) {
		return Optional.ofNullable(
			queryFactory.selectFrom(licenseGrade)
				.join(license).on(license.licenseGradeId.eq(licenseGrade.id))
				.where(
					eqLicenseId(licenseId)
				)
				.fetchFirst()
		);
	}

	private BooleanExpression eqLicenseId(Long licenseId) {
		if (licenseId == null) {
			return null;
		}

		return license.id.eq(licenseId);
	}
}
