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

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseRevisionResponseDto;
import com.virnect.account.domain.model.OrganizationLicense;
import com.virnect.account.port.outbound.OrganizationLicenseRevisionRepository;

@Repository
@RequiredArgsConstructor
public class OrganizationLicenseRevisionRepositoryImpl implements OrganizationLicenseRevisionRepository {
	private final EntityManager entityManager;

	@Override
	public List<OrganizationLicenseRevisionResponseDto> getOrganizationLicenseRevisionResponses(
		Long organizationLicenseId
	) {
		AuditOrder sorting = AuditEntity.revisionNumber().desc();

		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> organizationLicenseRevisions = reader.createQuery()
			.forRevisionsOfEntity(OrganizationLicense.class, false, true)
			.add(AuditEntity.id().eq(organizationLicenseId))
			.addOrder(sorting)
			.getResultList();

		return organizationLicenseRevisions.stream()
			.map(revision -> {
				RevisionType revisionType = (RevisionType)revision[2];
				return OrganizationLicenseRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(OrganizationLicense)revision[0]
				);
			})
			.collect(Collectors.toList());
	}
}
