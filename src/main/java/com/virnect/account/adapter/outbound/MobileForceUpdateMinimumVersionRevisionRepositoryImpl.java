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

import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionRevisionResponseDto;
import com.virnect.account.domain.model.MobileForceUpdateMinimumVersion;
import com.virnect.account.port.outbound.MobileForceUpdateMinimumVersionRevisionRepository;

@Repository
@RequiredArgsConstructor
public class MobileForceUpdateMinimumVersionRevisionRepositoryImpl implements
	MobileForceUpdateMinimumVersionRevisionRepository {

	private final EntityManager entityManager;

	@Override
	public Page<MobileForceUpdateMinimumVersionRevisionResponseDto> getMobileForceUpdateMinimumVersionRevisions(
		Pageable pageable
	) {
		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> mobileForceUpdateMinimumVersionRevisions = reader.createQuery()
			.forRevisionsOfEntity(MobileForceUpdateMinimumVersion.class, false, true)
			.addOrder(AuditEntity.revisionNumber().desc())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(MobileForceUpdateMinimumVersion.class, true, true)
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<MobileForceUpdateMinimumVersionRevisionResponseDto> mobileForceUpdateMinimumVersionRevisionResponseDtos =
			mobileForceUpdateMinimumVersionRevisions.stream()
				.map(mobileForceUpdateMinimumVersionRevision -> {
					RevisionType revisionType = (RevisionType)mobileForceUpdateMinimumVersionRevision[2];
					return MobileForceUpdateMinimumVersionRevisionResponseDto.of(
						revisionType.getRepresentation(),
						(MobileForceUpdateMinimumVersion)mobileForceUpdateMinimumVersionRevision[0]
					);
				})
				.collect(Collectors.toList());

		return new PageImpl<>(mobileForceUpdateMinimumVersionRevisionResponseDtos, pageable, count);
	}
}
