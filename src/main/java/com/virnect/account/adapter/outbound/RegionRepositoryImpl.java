package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QServiceRegion.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.model.ServiceRegion;
import com.virnect.account.port.outbound.RegionRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class RegionRepositoryImpl  implements RegionRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<ServiceRegion> getRegion(Long regionId) {
		return Optional.ofNullable(
			queryFactory.selectFrom(serviceRegion)
				.where(
					serviceRegion.id.eq(regionId)
				)
				.fetchOne());
	}
}
