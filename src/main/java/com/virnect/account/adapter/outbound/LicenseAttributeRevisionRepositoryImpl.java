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

import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeRevisionResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.model.LicenseAttribute;
import com.virnect.account.port.outbound.LicenseAttributeRevisionRepository;

@Repository
@RequiredArgsConstructor
public class LicenseAttributeRevisionRepositoryImpl implements LicenseAttributeRevisionRepository {

	private final EntityManager entityManager;

	@Override
	public Page<LicenseAttributeRevisionResponseDto> getLicenseAttributeRevisionResponses(
		Long licenseId, LicenseAttributeType licenseAttributeType, Pageable pageable
	) {
		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> licenseAttributeRevisions = reader.createQuery()
			.forRevisionsOfEntity(LicenseAttribute.class, false, true)
			.add(AuditEntity.property("licenseId").eq(licenseId))
			.add(AuditEntity.property("attributeType").eq(licenseAttributeType))
			.addOrder(AuditEntity.revisionNumber().desc())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(LicenseAttribute.class, true, true)
			.add(AuditEntity.property("licenseId").eq(licenseId))
			.add(AuditEntity.property("attributeType").eq(licenseAttributeType))
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<LicenseAttributeRevisionResponseDto> licenseAttributeRevisionResponses = licenseAttributeRevisions.stream()
			.map(licenseAttributeRevision -> {
				RevisionType revisionType = (RevisionType)licenseAttributeRevision[2];
				return LicenseAttributeRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(LicenseAttribute)licenseAttributeRevision[0]
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(licenseAttributeRevisionResponses, pageable, count);
	}

	@Override
	public Page<LicenseAttributeRevisionResponseDto> getLicenseAdditionalAttributeRevisions(
		Long licenseId, LicenseAdditionalAttributeType licenseAdditionalAttributeType, Pageable pageable
	) {
		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> licenseAdditionalAttributeRevisions = reader.createQuery()
			.forRevisionsOfEntity(LicenseAttribute.class, false, true)
			.add(AuditEntity.property("licenseId").eq(licenseId))
			.add(AuditEntity.property("additionalAttributeType").eq(licenseAdditionalAttributeType))
			.addOrder(AuditEntity.revisionNumber().desc())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(LicenseAttribute.class, true, true)
			.add(AuditEntity.property("licenseId").eq(licenseId))
			.add(AuditEntity.property("additionalAttributeType").eq(licenseAdditionalAttributeType))
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<LicenseAttributeRevisionResponseDto> licenseAttributeRevisionResponses = licenseAdditionalAttributeRevisions.stream()
			.map(licenseAdditionalAttributeRevision -> {
				RevisionType revisionType = (RevisionType)licenseAdditionalAttributeRevision[2];
				return LicenseAttributeRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(LicenseAttribute)licenseAdditionalAttributeRevision[0]
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(licenseAttributeRevisionResponses, pageable, count);
	}
}
