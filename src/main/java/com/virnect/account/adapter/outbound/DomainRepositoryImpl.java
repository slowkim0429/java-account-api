package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QDomain.*;
import static com.virnect.account.domain.model.QServiceRegionLocaleMapping.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.domain.DomainSearchDto;
import com.virnect.account.adapter.inbound.dto.response.DomainResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QDomainResponseDto;
import com.virnect.account.domain.model.Domain;
import com.virnect.account.port.outbound.DomainRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class DomainRepositoryImpl implements DomainRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Domain> getDomainByRegionIdAndRecordName(Long regionId, String recordName) {
		return Optional.ofNullable(
			queryFactory.selectFrom(domain)
				.where(
					domain.serviceRegionId.eq(regionId),
					domain.recordName.eq(recordName)
				)
				.fetchOne()
		);
	}

	@Override
	public Page<DomainResponseDto> getDomainResponses(
		DomainSearchDto domainSearchDto, Pageable pageable
	) {
		QueryResults<DomainResponseDto> pageResult = queryFactory
			.select(new QDomainResponseDto(
				domain.id,
				domain.recordName,
				domain.url,
				domain.serviceRegionId,
				domain.createdDate
			))
			.from(domain)
			.join(serviceRegionLocaleMapping).on(domain.serviceRegionId.eq(serviceRegionLocaleMapping.serviceRegionId))
			.where(
				serviceRegionLocaleMapping.id.eq(domainSearchDto.getLocaleId())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(pageResult.getResults(), pageable, pageResult.getTotal());
	}
}