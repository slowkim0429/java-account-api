package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QOrganization.*;
import static com.virnect.account.domain.model.QUser.*;

import java.time.ZonedDateTime;
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

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationSearchDto;
import com.virnect.account.domain.enumclass.OrganizationStatus;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.port.outbound.OrganizationRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class OrganizationRepositoryImpl implements OrganizationRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqId(Long id) {
		if (id == null || id <= 0) {
			return null;
		}
		return organization.id.eq(id);
	}

	private BooleanExpression eqEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return null;
		}
		return organization.email.eq(email);
	}

	private BooleanExpression likeName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		return organization.name.like(name + "%");
	}

	private BooleanExpression eqStatus(String status) {
		if (StringUtils.isBlank(status)) {
			return null;
		}
		return organization.status.eq(OrganizationStatus.valueOf(status));
	}

	private BooleanExpression startsWithStateName(String stateName) {
		if (StringUtils.isBlank(stateName)) {
			return null;
		}
		return organization.stateName.toLowerCase().startsWith(stateName.toLowerCase());
	}

	private BooleanExpression startsWithLocaleName(String localeName) {
		if (StringUtils.isBlank(localeName)) {
			return null;
		}
		return organization.localeName.toLowerCase().startsWith(localeName.toLowerCase());
	}

	private BooleanExpression startsWithProvince(String province) {
		if (StringUtils.isBlank(province)) {
			return null;
		}
		return organization.province.startsWith(province);
	}

	private BooleanExpression startsWithEmailDomain(String emailDomain) {
		if (StringUtils.isBlank(emailDomain)) {
			return null;
		}
		return organization.emailDomain.startsWith(emailDomain);
	}

	@Override
	public Optional<Organization> getOrganization(Long organizationId) {
		return Optional.ofNullable(
			queryFactory.selectFrom(organization)
				.where(
					eqId(organizationId)
				)
				.fetchOne());
	}

	private BooleanExpression goeCreatedDate(ZonedDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return organization.createdDate.goe(dateTime);
	}

	private BooleanExpression loeCreatedDate(ZonedDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return organization.createdDate.loe(dateTime);
	}

	@Override
	public Page<Organization> getOrganizations(
		OrganizationSearchDto organizationSearchDto, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable
	) {
		QueryResults<Organization> pagingResult = queryFactory
			.selectFrom(organization)
			.join(user).on(organization.id.eq(user.organizationId))
			.where(
				eqStatus(organizationSearchDto.getStatus()),
				startsWithStateName(organizationSearchDto.getStateName()),
				startsWithLocaleName(organizationSearchDto.getLocaleName()),
				startsWithProvince(organizationSearchDto.getProvince()),
				eqId(organizationSearchDto.getOrganizationId()),
				eqEmail(organizationSearchDto.getContractEmail()),
				startsWithEmailDomain(organizationSearchDto.getEmailDomain()),
				likeName(organizationSearchDto.getOrganizationName()),
				goeCreatedDate(startDate),
				loeCreatedDate(endDate)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(organization.id.desc())
			.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}

}
