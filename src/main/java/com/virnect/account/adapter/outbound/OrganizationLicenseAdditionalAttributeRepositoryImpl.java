package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QOrganizationLicenseAdditionalAttribute.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QOrganizationLicenseAdditionalAttributeResponseDto;
import com.virnect.account.domain.model.OrganizationLicenseAdditionalAttribute;
import com.virnect.account.port.outbound.OrganizationLicenseAdditionalAttributeRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class OrganizationLicenseAdditionalAttributeRepositoryImpl implements
	OrganizationLicenseAdditionalAttributeRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<OrganizationLicenseAdditionalAttribute> getOrganizationLicenseAdditionalAttribute(
		Long organizationContractId
	) {
		return Optional.ofNullable(
			queryFactory.selectFrom(organizationLicenseAdditionalAttribute)
				.where(
					organizationLicenseAdditionalAttribute.organizationContractId.eq(organizationContractId)
				)
				.fetchOne());
	}

	@Override
	public List<OrganizationLicenseAdditionalAttribute> getOrganizationLicenseAdditionalAttributes(
		Long subscriptionOrganizationLicenseId
	) {
		return queryFactory
			.selectFrom(organizationLicenseAdditionalAttribute)
			.where(
				organizationLicenseAdditionalAttribute.subscriptionOrganizationLicenseId.eq(
					subscriptionOrganizationLicenseId)
			).fetch();
	}

	@Override
	public List<OrganizationLicenseAdditionalAttributeResponseDto> getOrganizationLicenseAdditionalAttributeResponses(
		Long subscriptionOrganizationLicenseId
	) {
		return queryFactory.select(new QOrganizationLicenseAdditionalAttributeResponseDto(
					organizationLicenseAdditionalAttribute.id
					, organizationLicenseAdditionalAttribute.contractId
					, organizationLicenseAdditionalAttribute.licenseAttributeId
					, organizationLicenseAdditionalAttribute.licenseAdditionalAttributeType
					, organizationLicenseAdditionalAttribute.dataType
					, organizationLicenseAdditionalAttribute.dataValue
					, organizationLicenseAdditionalAttribute.status
					, organizationLicenseAdditionalAttribute.createdDate
					, organizationLicenseAdditionalAttribute.updatedDate
					, organizationLicenseAdditionalAttribute.createdBy
					, organizationLicenseAdditionalAttribute.lastModifiedBy
				)
			)
			.from(organizationLicenseAdditionalAttribute)
			.where(
				organizationLicenseAdditionalAttribute.subscriptionOrganizationLicenseId.eq(
					subscriptionOrganizationLicenseId)
			)
			.orderBy(organizationLicenseAdditionalAttribute.id.desc())
			.fetch();
	}

}
