package com.virnect.account.adapter.outbound;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.order.AuditOrder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeRevisionResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.model.OrganizationLicenseAdditionalAttribute;
import com.virnect.account.port.outbound.OrganizationLicenseAdditionalAttributeRevisionRepository;

@Repository
@RequiredArgsConstructor
public class OrganizationLicenseAdditionalAttributeRevisionRepositoryImpl implements
	OrganizationLicenseAdditionalAttributeRevisionRepository {
	private final EntityManager entityManager;

	@Override
	public List<OrganizationLicenseAdditionalAttributeRevisionResponseDto> getRevisionResponses(
		Long organizationLicenseId,
		LicenseAdditionalAttributeType licenseAdditionalAttributeType
	) {
		AuditOrder sorting = AuditEntity.revisionNumber().desc();

		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> revisions = reader.createQuery()
			.forRevisionsOfEntity(OrganizationLicenseAdditionalAttribute.class, false, true)
			.add(AuditEntity.property("subscriptionOrganizationLicenseId").eq(organizationLicenseId))
			.add(AuditEntity.property("licenseAdditionalAttributeType").eq(licenseAdditionalAttributeType))
			.addOrder(sorting)
			.getResultList();

		return revisions.stream()
			.map(revision -> {
				RevisionType revisionType = (RevisionType)revision[2];
				return OrganizationLicenseAdditionalAttributeRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(OrganizationLicenseAdditionalAttribute)revision[0]
				);
			})
			.collect(Collectors.toList());	}
}
