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

import com.virnect.account.adapter.inbound.dto.response.authoritygroup.AuthorityGroupRevisionResponseDto;
import com.virnect.account.domain.model.AuthorityGroup;
import com.virnect.account.port.outbound.AuthorityGroupRevisionRepository;

@Repository
@RequiredArgsConstructor
public class AuthorityGroupRevisionRepositoryImpl implements AuthorityGroupRevisionRepository {

	private final EntityManager entityManager;

	@Override
	public Page<AuthorityGroupRevisionResponseDto> getAuthorityGroupRevisionResponses(
		Long authorityGroupId, Pageable pageable
	) {
		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> authorityGroupRevisions = reader.createQuery()
			.forRevisionsOfEntity(AuthorityGroup.class, false, true)
			.add(AuditEntity.id().eq(authorityGroupId))
			.addOrder(AuditEntity.revisionNumber().desc())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(AuthorityGroup.class, true, true)
			.add(AuditEntity.id().eq(authorityGroupId))
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<AuthorityGroupRevisionResponseDto> authorityGroupRevisionResponseDtos = authorityGroupRevisions.stream()
			.map(authorityGroupRevision -> {
				RevisionType revisionType = (RevisionType)authorityGroupRevision[2];
				return AuthorityGroupRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(AuthorityGroup)authorityGroupRevision[0]
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(authorityGroupRevisionResponseDtos, pageable, count);
	}
}
