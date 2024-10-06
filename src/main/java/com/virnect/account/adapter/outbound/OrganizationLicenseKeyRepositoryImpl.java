package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QOrganizationLicenseKey.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseKeySearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseKeyResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QOrganizationLicenseKeyResponseDto;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.OrganizationLicenseKey;
import com.virnect.account.port.outbound.OrganizationLicenseKeyRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class OrganizationLicenseKeyRepositoryImpl implements OrganizationLicenseKeyRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqOrganizationId(Long organizationId) {
		if (organizationId == null || organizationId <= 0) {
			return null;
		}
		return organizationLicenseKey.organizationId.eq(organizationId);
	}

	private BooleanExpression eqOrganizationLicenseId(Long organizationLicenseId) {
		if (organizationLicenseId == null || organizationLicenseId <= 0) {
			return null;
		}
		return organizationLicenseKey.organizationLicenseId.eq(organizationLicenseId);
	}

	private BooleanExpression eqOrganizationLicenseKeyId(Long organizationLicenseKeyId) {
		if (organizationLicenseKeyId == null || organizationLicenseKeyId <= 0) {
			return null;
		}
		return organizationLicenseKey.id.eq(organizationLicenseKeyId);
	}

	private BooleanExpression eqUseStatus(UseStatus useStatus) {
		if (useStatus == null) {
			return null;
		}
		return organizationLicenseKey.useStatus.eq(useStatus);
	}

	@Override
	public Optional<OrganizationLicenseKey> getOrganizationLicenseKey(
		Long organizationId, Long organizationLicenseKeyId, UseStatus useStatus
	) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(organizationLicenseKey)
				.where(
					eqOrganizationId(organizationId),
					eqOrganizationLicenseKeyId(organizationLicenseKeyId),
					eqUseStatus(useStatus)
				)
				.fetchOne()
		);
	}

	@Override
	public Page<OrganizationLicenseKeyResponseDto> getOrganizationLicenseKeyResponses(
		OrganizationLicenseKeySearchDto organizationLicenseKeySearchDto, Pageable pageable
	) {
		QueryResults<OrganizationLicenseKeyResponseDto> pagingResult = queryFactory
			.select(new QOrganizationLicenseKeyResponseDto(
					organizationLicenseKey.id,
					organizationLicenseKey.organizationId,
					organizationLicenseKey.organizationLicenseId,
					organizationLicenseKey.email,
					organizationLicenseKey.licenseKey,
					organizationLicenseKey.useStatus,
					organizationLicenseKey.createdDate,
					organizationLicenseKey.updatedDate,
					organizationLicenseKey.createdBy,
					organizationLicenseKey.lastModifiedBy
				)
			)
			.from(organizationLicenseKey)
			.where(
				eqOrganizationId(organizationLicenseKeySearchDto.getOrganizationId()),
				eqOrganizationLicenseId(organizationLicenseKeySearchDto.getOrganizationLicenseId())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(organizationLicenseKey.id.desc())
			.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}
}
