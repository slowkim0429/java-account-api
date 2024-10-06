package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QOrganizationLicenseTrackSdkUsageHistory.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseTrackSdkUsageSearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseTrackSdkUsageResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QOrganizationLicenseTrackSdkUsageResponseDto;
import com.virnect.account.port.outbound.OrganizationLicenseTrackSdkUsageHistoryRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class OrganizationLicenseTrackSdkUsageHistoryRepositoryImpl
	implements OrganizationLicenseTrackSdkUsageHistoryRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqOrganizationLicenseKeyId(Long organizationLicenseKeyId) {
		if (organizationLicenseKeyId == null || organizationLicenseKeyId <= 0) {
			return null;
		}
		return organizationLicenseTrackSdkUsageHistory.organizationLicenseKeyId.eq(organizationLicenseKeyId);
	}

	@Override
	public Page<OrganizationLicenseTrackSdkUsageResponseDto> getTrackSdkHistoryResponses(
		OrganizationLicenseTrackSdkUsageSearchDto organizationLicenseTrackSdkUsageSearchDto, PageRequest pageable
	) {
		QueryResults<OrganizationLicenseTrackSdkUsageResponseDto> pageResult = queryFactory
			.select(new QOrganizationLicenseTrackSdkUsageResponseDto(
				organizationLicenseTrackSdkUsageHistory.id,
				organizationLicenseTrackSdkUsageHistory.organizationLicenseKeyId,
				organizationLicenseTrackSdkUsageHistory.content,
				organizationLicenseTrackSdkUsageHistory.createdDate,
				organizationLicenseTrackSdkUsageHistory.updatedDate,
				organizationLicenseTrackSdkUsageHistory.lastModifiedBy,
				organizationLicenseTrackSdkUsageHistory.createdBy
			))
			.from(organizationLicenseTrackSdkUsageHistory)
			.where(
				eqOrganizationLicenseKeyId(organizationLicenseTrackSdkUsageSearchDto.getOrganizationLicenseKeyId())
			)
			.orderBy(organizationLicenseTrackSdkUsageHistory.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();
		return new PageImpl<>(pageResult.getResults(), pageable, pageResult.getTotal());
	}
}
