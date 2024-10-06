package com.virnect.account.adapter.outbound;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.order.AuditOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.RevisionSort;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.ItemRevisionResponseDto;
import com.virnect.account.domain.model.Item;
import com.virnect.account.port.outbound.ItemRevisionRepository;

@Repository
@RequiredArgsConstructor
public class ItemRevisionRepositoryImpl implements ItemRevisionRepository {
	private final EntityManager entityManager;

	@Override
	public Page<ItemRevisionResponseDto> getItemRevisionResponses(Long itemId, Pageable pageable) {
		AuditOrder sorting = RevisionSort.getRevisionDirection(pageable.getSort()).isDescending()
			? AuditEntity.revisionNumber().desc()
			: AuditEntity.revisionNumber().asc();

		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> itemRevisions = reader.createQuery().forRevisionsOfEntity(Item.class, false, true)
			.add(AuditEntity.id().eq(itemId))
			.addOrder(sorting)
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(Item.class, true, true)
			.add(AuditEntity.id().eq(itemId))
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<ItemRevisionResponseDto> itemRevisionResponses = itemRevisions.stream()
			.map(itemRevision -> {
				RevisionType revisionType = (RevisionType)itemRevision[2];
				return ItemRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(Item)itemRevision[0]
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(itemRevisionResponses, pageable, count);
	}
}
