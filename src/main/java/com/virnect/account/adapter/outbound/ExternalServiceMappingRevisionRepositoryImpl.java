package com.virnect.account.adapter.outbound;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.ExternalServiceMappingRevisionResponseDto;
import com.virnect.account.domain.model.ExternalServiceMapping;
import com.virnect.account.port.outbound.ExternalServiceMappingRevisionRepository;

@Repository
@RequiredArgsConstructor
public class ExternalServiceMappingRevisionRepositoryImpl implements ExternalServiceMappingRevisionRepository {

	private final EntityManager entityManager;

	@Override
	public Page<ExternalServiceMappingRevisionResponseDto> getRevisions(
		Long externalServiceMappingId, Pageable pageable
	) {
		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> externalServiceMappingRevisions = reader.createQuery()
			.forRevisionsOfEntity(ExternalServiceMapping.class, false, true)
			.add(AuditEntity.id().eq(externalServiceMappingId))
			.addOrder(AuditEntity.revisionNumber().desc())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(ExternalServiceMapping.class, true, true)
			.add(AuditEntity.id().eq(externalServiceMappingId))
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<ExternalServiceMappingRevisionResponseDto> externalServiceMappingRevisionResponseDtos = externalServiceMappingRevisions
			.stream()
			.map(externalServiceMappingRevision -> {
				RevisionType revisionType = (RevisionType)externalServiceMappingRevision[2];
				return ExternalServiceMappingRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(ExternalServiceMapping)externalServiceMappingRevision[0]
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(externalServiceMappingRevisionResponseDtos, pageable, count);
	}
}
