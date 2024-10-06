package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QOrganizationLicenseAttribute.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QOrganizationLicenseAttributeDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QOrganizationLicenseAttributeResponseDto;
import com.virnect.account.domain.model.OrganizationLicenseAttribute;
import com.virnect.account.port.outbound.OrganizationLicenseAttributeRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class OrganizationLicenseAttributeRepositoryImpl implements OrganizationLicenseAttributeRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<OrganizationLicenseAttribute> getOrganizationLicenseAttributes(Long organizationLicenseId) {
		return queryFactory
			.selectFrom(organizationLicenseAttribute)
			.where(
				organizationLicenseAttribute.organizationLicenseId.eq(organizationLicenseId)
			).fetch();

	}

	@Override
	public List<OrganizationLicenseAttributeResponseDto> getOrganizationLicenseAttributeResponses(
		Long organizationLicenseId
	) {
		return queryFactory
			.select(new QOrganizationLicenseAttributeResponseDto(
					organizationLicenseAttribute.id
					, organizationLicenseAttribute.licenseAttributeType
					, organizationLicenseAttribute.dataType
					, organizationLicenseAttribute.dataValue
					, organizationLicenseAttribute.status
				)
			)
			.from(organizationLicenseAttribute)
			.where(
				organizationLicenseAttribute.organizationLicenseId.eq(organizationLicenseId)
			)
			.orderBy(organizationLicenseAttribute.id.desc())
			.fetch();
	}

	@Override
	public List<OrganizationLicenseAttributeDetailResponseDto> getOrganizationLicenseAttributeDetailResponses(
		Long organizationLicenseId
	) {
		return queryFactory
			.select(new QOrganizationLicenseAttributeDetailResponseDto(
					organizationLicenseAttribute.id
					, organizationLicenseAttribute.organizationLicenseId
					, organizationLicenseAttribute.licenseAttributeId
					, organizationLicenseAttribute.licenseAttributeType
					, organizationLicenseAttribute.dataType
					, organizationLicenseAttribute.dataValue
					, organizationLicenseAttribute.status
					, organizationLicenseAttribute.createdDate
					, organizationLicenseAttribute.updatedDate
					, organizationLicenseAttribute.createdBy
					, organizationLicenseAttribute.lastModifiedBy
				)
			)
			.from(organizationLicenseAttribute)
			.where(
				organizationLicenseAttribute.organizationLicenseId.eq(organizationLicenseId)
			)
			.orderBy(organizationLicenseAttribute.id.desc())
			.fetch();
	}
}
