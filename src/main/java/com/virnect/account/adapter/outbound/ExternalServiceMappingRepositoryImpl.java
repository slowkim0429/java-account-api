package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QExternalServiceMapping.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.hubspot.ExternalServiceMappingSearchDto;
import com.virnect.account.adapter.inbound.dto.response.HubspotMappingResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QHubspotMappingResponseDto;
import com.virnect.account.domain.enumclass.InternalDomain;
import com.virnect.account.domain.model.ExternalServiceMapping;
import com.virnect.account.port.outbound.ExternalServiceMappingRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class ExternalServiceMappingRepositoryImpl implements ExternalServiceMappingRepositoryCustom {
	private final JPAQueryFactory query;

	private BooleanExpression eqId(Long id) {
		if (id == null) {
			return null;
		}
		return externalServiceMapping.id.eq(id);
	}

	private BooleanExpression eqInternalDomain(InternalDomain internalDomain) {
		if (internalDomain == null) {
			return null;
		}
		return externalServiceMapping.internalDomain.eq(internalDomain);
	}

	private BooleanExpression eqIsLatestMappingSucceeded(Boolean latestMappingSucceeded) {
		if (latestMappingSucceeded == null) {
			return null;
		}
		return externalServiceMapping.isLatestMappingSucceeded.eq(latestMappingSucceeded);
	}

	private BooleanExpression eqInternalMappingId(Long internalMappingId) {
		if (internalMappingId == null) {
			return null;
		}
		return externalServiceMapping.internalMappingId.eq(internalMappingId);
	}

	@Override
	public Optional<ExternalServiceMapping> getExternalServiceMapping(
		Long id, InternalDomain internalDomain, Long internalMappingId
	) {
		return Optional.ofNullable(
			query.selectFrom(externalServiceMapping)
				.where(
					eqId(id),
					eqInternalDomain(internalDomain),
					eqInternalMappingId(internalMappingId)
				)
				.fetchOne());
	}

	@Override
	public Page<HubspotMappingResponseDto> getExternalServiceMappingResponses(
		ExternalServiceMappingSearchDto externalServiceMappingSearchDto, Pageable pageable
	) {
		QueryResults<HubspotMappingResponseDto> pageResult = query
			.select(new QHubspotMappingResponseDto(
				externalServiceMapping.id
				, externalServiceMapping.externalDomain
				, externalServiceMapping.internalDomain
				, externalServiceMapping.externalMappingId
				, externalServiceMapping.internalMappingId
				, externalServiceMapping.isLatestMappingSucceeded
				, externalServiceMapping.isSyncable
				, externalServiceMapping.createdBy
				, externalServiceMapping.lastModifiedBy
				, externalServiceMapping.createdDate.stringValue()
				, externalServiceMapping.updatedDate.stringValue()
			))
			.where(eqInternalDomain(externalServiceMappingSearchDto.getInternalDomain())
				, eqIsLatestMappingSucceeded(externalServiceMappingSearchDto.getIsLatestMappingSucceeded())
				, eqInternalMappingId(externalServiceMappingSearchDto.getInternalMappingId()))
			.from(externalServiceMapping)
			.orderBy(externalServiceMapping.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(pageResult.getResults(), pageable, pageResult.getTotal());
	}

}
