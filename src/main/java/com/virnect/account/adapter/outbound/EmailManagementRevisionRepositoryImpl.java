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

import com.virnect.account.adapter.inbound.dto.response.EmailManagementRevisionResponseDto;
import com.virnect.account.domain.model.EmailCustomizingManagement;
import com.virnect.account.port.outbound.EmailManagementRevisionRepository;

@Repository
@RequiredArgsConstructor
public class EmailManagementRevisionRepositoryImpl implements EmailManagementRevisionRepository {

	private final EntityManager entityManager;

	@Override
	public Page<EmailManagementRevisionResponseDto> getEmailManagementRevisions(
		Long emailManagementId, Pageable pageable
	) {
		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> emailManagementRevisions = reader.createQuery()
			.forRevisionsOfEntity(EmailCustomizingManagement.class, false, true)
			.add(AuditEntity.id().eq(emailManagementId))
			.addOrder(AuditEntity.revisionNumber().desc())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(EmailCustomizingManagement.class, true, true)
			.add(AuditEntity.id().eq(emailManagementId))
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<EmailManagementRevisionResponseDto> emailManagementRevisionResponses = emailManagementRevisions.stream()
			.map(emailManagementRevision -> {
				RevisionType revisionType = (RevisionType)emailManagementRevision[2];
				return EmailManagementRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(EmailCustomizingManagement)emailManagementRevision[0]
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(emailManagementRevisionResponses, pageable, count);
	}
}
