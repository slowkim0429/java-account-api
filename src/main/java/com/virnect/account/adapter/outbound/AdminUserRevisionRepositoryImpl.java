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

import com.virnect.account.adapter.inbound.dto.response.AdminUserRevisionResponseDto;
import com.virnect.account.domain.model.AdminUser;
import com.virnect.account.port.outbound.AdminUserRevisionRepository;

@Repository
@RequiredArgsConstructor
public class AdminUserRevisionRepositoryImpl implements AdminUserRevisionRepository {
	private final EntityManager entityManager;

	@Override
	public Page<AdminUserRevisionResponseDto> getAdminUserRevisionResponses(Long adminUserId, Pageable pageable) {
		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> adminUserRevisions = reader.createQuery()
			.forRevisionsOfEntity(AdminUser.class, false, true)
			.add(AuditEntity.id().eq(adminUserId))
			.addOrder(AuditEntity.revisionNumber().desc())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(AdminUser.class, true, true)
			.add(AuditEntity.id().eq(adminUserId))
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<AdminUserRevisionResponseDto> adminUserRevisionResponseDtos = adminUserRevisions.stream()
			.map(adminUserRevision -> {
				RevisionType revisionType = (RevisionType)adminUserRevision[2];
				return AdminUserRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(AdminUser)adminUserRevision[0]
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(adminUserRevisionResponseDtos, pageable, count);
	}
}
