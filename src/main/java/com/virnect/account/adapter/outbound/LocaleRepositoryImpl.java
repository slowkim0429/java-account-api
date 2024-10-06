package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QServiceRegion.*;
import static com.virnect.account.domain.model.QServiceRegionLocaleMapping.*;

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

import com.virnect.account.adapter.inbound.dto.request.locale.LocaleSearchDto;
import com.virnect.account.adapter.inbound.dto.response.LocaleResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QLocaleResponseDto;
import com.virnect.account.domain.model.ServiceRegionLocaleMapping;
import com.virnect.account.port.outbound.LocaleRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class LocaleRepositoryImpl implements LocaleRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqCode(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		return serviceRegionLocaleMapping.code.eq(code);
	}

	private BooleanExpression eqRegionCode(String regionCode) {
		if (StringUtils.isBlank(regionCode)) {
			return null;
		}
		return serviceRegion.code.eq(regionCode);
	}

	private BooleanExpression eqServiceRegionLocaleMappingId(Long id) {
		if (id == null) {
			return null;
		}
		return serviceRegionLocaleMapping.id.eq(id);
	}

	@Override
	public Page<LocaleResponseDto> getLocalesResponse(
		LocaleSearchDto localeSearchDto, Pageable pageable
	) {
		QueryResults<LocaleResponseDto> pagingResult =
			queryFactory.select(new QLocaleResponseDto(
						serviceRegionLocaleMapping.id
						, serviceRegionLocaleMapping.name
						, serviceRegionLocaleMapping.code
						, serviceRegionLocaleMapping.countryCallingCode
						, serviceRegion.id
						, serviceRegion.name
						, serviceRegion.code
						, serviceRegion.awsCode
						, serviceRegionLocaleMapping.createdDate
						, serviceRegionLocaleMapping.updatedDate
					)
				)
				.from(serviceRegionLocaleMapping)
				.join(serviceRegion).on(serviceRegion.id.eq(serviceRegionLocaleMapping.serviceRegionId))
				.where(
					eqCode(localeSearchDto.getCode()),
					eqRegionCode(localeSearchDto.getRegionCode())
				)
				.orderBy(serviceRegionLocaleMapping.id.asc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}

	@Override
	public Optional<ServiceRegionLocaleMapping> getLocaleById(Long id) {
		return Optional.ofNullable(
			queryFactory.selectFrom(serviceRegionLocaleMapping)
				.where(
					eqServiceRegionLocaleMappingId(id)
				)
				.fetchOne());
	}

	@Override
	public Optional<ServiceRegionLocaleMapping> getLocale(String code) {
		return Optional.ofNullable(
			queryFactory.selectFrom(serviceRegionLocaleMapping)
				.where(
					serviceRegionLocaleMapping.code.eq(code)
				)
				.fetchOne());
	}
}
