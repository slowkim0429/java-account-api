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

import com.virnect.account.adapter.inbound.dto.response.invite.InviteRevisionResponseDto;
import com.virnect.account.domain.model.Invite;
import com.virnect.account.port.outbound.InviteRevisionRepository;

@Repository
@RequiredArgsConstructor
public class InviteRevisionRepositoryImpl implements InviteRevisionRepository {
	private final EntityManager entityManager;

	@Override
	public List<InviteRevisionResponseDto> getInviteRevisions(Long inviteId) {
		AuditOrder sorting = AuditEntity.revisionNumber().desc();

		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> inviteRevisions = reader.createQuery()
			.forRevisionsOfEntity(Invite.class, false, true)
			.add(AuditEntity.id().eq(inviteId))
			.addOrder(sorting)
			.getResultList();

		return inviteRevisions.stream()
			.map(inviteRevision -> {
				RevisionType revisionType = (RevisionType)inviteRevision[2];
				return InviteRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(Invite)inviteRevision[0]
				);
			})
			.collect(Collectors.toList());
	}
}
