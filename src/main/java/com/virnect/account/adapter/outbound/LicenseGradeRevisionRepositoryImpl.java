package com.virnect.account.adapter.outbound;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.order.AuditOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.grade.LicenseGradeRevisionResponseDto;
import com.virnect.account.domain.model.LicenseGrade;
import com.virnect.account.port.outbound.LicenseGradeRevisionRepository;

@Repository
@RequiredArgsConstructor
public class LicenseGradeRevisionRepositoryImpl implements LicenseGradeRevisionRepository {
	private final EntityManager entityManager;

	@Override
	public List<LicenseGradeRevisionResponseDto> getLicenseGradeRevisionResponses(
		Long licenseGradeId, Pageable pageable
	) {
		AuditOrder sorting = AuditEntity.revisionNumber().desc();

		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> licenseGradeRevisions = reader.createQuery()
			.forRevisionsOfEntity(LicenseGrade.class, false, true)
			.add(AuditEntity.id().eq(licenseGradeId))
			.addOrder(sorting)
			.getResultList();

		return licenseGradeRevisions.stream()
			.map(licenseGradeRevision -> {
				RevisionType revisionType = (RevisionType)licenseGradeRevision[2];
				return LicenseGradeRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(LicenseGrade)licenseGradeRevision[0]
				);
			})
			.collect(Collectors.toList());
	}
}
