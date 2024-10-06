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

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeRevisionResponseDto;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.model.OrganizationLicenseAttribute;
import com.virnect.account.port.outbound.OrganizationLicenseAttributeRevisionRepository;

@Repository
@RequiredArgsConstructor
public class OrganizationLicenseAttributeRevisionRepositoryImpl implements
	OrganizationLicenseAttributeRevisionRepository {
	private final EntityManager entityManager;

	@Override
	public List<OrganizationLicenseAttributeRevisionResponseDto> getRevisionResponses(
		Long organizationLicenseId, LicenseAttributeType licenseAttributeType
	) {
		AuditOrder sorting = AuditEntity.revisionNumber().desc();

		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> revisions = reader.createQuery()
			.forRevisionsOfEntity(OrganizationLicenseAttribute.class, false, true)
			.add(AuditEntity.property("organizationLicenseId").eq(organizationLicenseId))
			.add(AuditEntity.property("licenseAttributeType").eq(licenseAttributeType))
			.addOrder(sorting)
			.getResultList();

		return revisions.stream()
			.map(revision -> {
				RevisionType revisionType = (RevisionType)revision[2];
				return OrganizationLicenseAttributeRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(OrganizationLicenseAttribute)revision[0]
				);
			})
			.collect(Collectors.toList());	}
}
