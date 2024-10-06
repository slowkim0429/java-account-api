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

import com.virnect.account.adapter.inbound.dto.response.OrganizationRevisionResponseDto;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.port.outbound.OrganizationRevisionRepository;

@Repository
@RequiredArgsConstructor
public class OrganizationRevisionRepositoryImpl implements OrganizationRevisionRepository {

	private final EntityManager entityManager;

	@Override
	public List<OrganizationRevisionResponseDto> getOrganizationRevisionResponses(Long organizationId) {
		AuditOrder sorting = AuditEntity.revisionNumber().desc();

		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> organizationRevisions = reader.createQuery()
			.forRevisionsOfEntity(Organization.class, false, true)
			.add(AuditEntity.id().eq(organizationId))
			.addOrder(sorting)
			.getResultList();

		return organizationRevisions.stream()
			.map(organizationRevision -> {
				RevisionType revisionType = (RevisionType)organizationRevision[2];
				return OrganizationRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(Organization)organizationRevision[0]
				);
			})
			.collect(Collectors.toList());
	}
}
