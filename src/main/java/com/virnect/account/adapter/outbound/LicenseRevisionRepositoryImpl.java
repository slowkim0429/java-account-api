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

import com.virnect.account.adapter.inbound.dto.response.LicenseRevisionResponseDto;
import com.virnect.account.domain.model.License;
import com.virnect.account.port.outbound.LicenseRevisionRepository;

@Repository
@RequiredArgsConstructor
public class LicenseRevisionRepositoryImpl implements LicenseRevisionRepository {
	private final EntityManager entityManager;

	@Override
	public Page<LicenseRevisionResponseDto> getLicenseRevisionResponses(Long licenseId, Pageable pageable) {
		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> licenseRevisions = reader.createQuery().forRevisionsOfEntity(License.class, false, true)
			.add(AuditEntity.id().eq(licenseId))
			.addOrder(AuditEntity.revisionNumber().desc())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(License.class, true, true)
			.add(AuditEntity.id().eq(licenseId))
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<LicenseRevisionResponseDto> licenseRevisionResponses = licenseRevisions.stream()
			.map(licenseRevision -> {
				RevisionType revisionType = (RevisionType)licenseRevision[2];
				return LicenseRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(License)licenseRevision[0]
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(licenseRevisionResponses, pageable, count);
	}
}
