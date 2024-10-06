package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QServiceTimeZone.*;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.QServiceTimeZoneResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ServiceTimeZoneResponseDto;
import com.virnect.account.domain.model.ServiceTimeZone;
import com.virnect.account.port.outbound.ServiceTimeZoneRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class ServiceTimeZoneRepositoryImpl implements ServiceTimeZoneRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<ServiceTimeZone> getServiceTimeZone(String localeCode) {
		return getServiceTimeZone(localeCode, null);
	}

	@Override
	public Optional<ServiceTimeZone> getServiceTimeZone(String localeCode, String zoneId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(serviceTimeZone)
				.where(
					eqLocaleCode(localeCode),
					eqZoneId(zoneId)
				)
				.fetchOne()
		);
	}

	@Override
	public Page<ServiceTimeZoneResponseDto> getServiceTimeZoneResponses(Pageable pageable) {
		QueryResults<ServiceTimeZoneResponseDto> pagingResult =
			queryFactory.select(new QServiceTimeZoneResponseDto(
						serviceTimeZone.id,
						serviceTimeZone.localeCode,
						serviceTimeZone.zoneId,
						serviceTimeZone.utcOffset
					)
				)
				.from(serviceTimeZone)
				.orderBy(serviceTimeZone.id.asc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}

	private BooleanExpression eqLocaleCode(String localeCode) {
		if (StringUtils.isBlank(localeCode)) {
			return null;
		}
		return serviceTimeZone.localeCode.eq(localeCode);
	}

	private BooleanExpression eqZoneId(String zoneId) {
		if (StringUtils.isBlank(zoneId)) {
			return null;
		}
		return serviceTimeZone.zoneId.eq(zoneId);
	}
}
