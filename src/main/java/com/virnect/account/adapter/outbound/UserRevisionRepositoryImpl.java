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

import com.virnect.account.adapter.inbound.dto.response.UserRevisionResponseDto;
import com.virnect.account.domain.model.User;
import com.virnect.account.port.outbound.UserRevisionRepository;

@Repository
@RequiredArgsConstructor
public class UserRevisionRepositoryImpl implements UserRevisionRepository {

	private final EntityManager entityManager;

	@Override
	public List<UserRevisionResponseDto> getUserRevisionResponses(Long userId) {
		AuditOrder sorting = AuditEntity.revisionNumber().desc();

		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> userRevisions = reader.createQuery()
			.forRevisionsOfEntity(User.class, false, true)
			.add(AuditEntity.id().eq(userId))
			.addOrder(sorting)
			.getResultList();

		return userRevisions.stream()
			.map(userRevision -> {
				RevisionType revisionType = (RevisionType)userRevision[2];
				return UserRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(User)userRevision[0]
				);
			})
			.collect(Collectors.toList());
	}
}
