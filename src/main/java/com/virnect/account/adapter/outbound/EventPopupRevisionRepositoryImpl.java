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

import com.virnect.account.adapter.inbound.dto.response.EventPopupRevisionResponseDto;
import com.virnect.account.domain.model.EventPopup;
import com.virnect.account.port.outbound.EventPopupRevisionRepository;

@Repository
@RequiredArgsConstructor
public class EventPopupRevisionRepositoryImpl implements EventPopupRevisionRepository {
	private final EntityManager entityManager;

	@Override
	public Page<EventPopupRevisionResponseDto> getEventPopupRevisionResponses(Long eventPopupId, Pageable pageable) {
		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> eventPopupRevisions = reader.createQuery().forRevisionsOfEntity(EventPopup.class, false, true)
			.add(AuditEntity.id().eq(eventPopupId))
			.addOrder(AuditEntity.revisionNumber().desc())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(EventPopup.class, true, true)
			.add(AuditEntity.id().eq(eventPopupId))
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<EventPopupRevisionResponseDto> eventPopupRevisionResponses = eventPopupRevisions.stream()
			.map(eventPopupRevision -> {
				RevisionType revisionType = (RevisionType)eventPopupRevision[2];
				return EventPopupRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(EventPopup)eventPopupRevision[0]
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(eventPopupRevisionResponses, pageable, count);
	}
}
