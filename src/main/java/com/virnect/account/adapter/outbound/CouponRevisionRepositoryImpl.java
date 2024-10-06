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

import com.virnect.account.adapter.inbound.dto.response.CouponRevisionResponseDto;
import com.virnect.account.domain.model.Coupon;
import com.virnect.account.port.outbound.CouponRevisionRepository;

@Repository
@RequiredArgsConstructor
public class CouponRevisionRepositoryImpl implements CouponRevisionRepository {
	private final EntityManager entityManager;

	@Override
	public Page<CouponRevisionResponseDto> getCouponRevisionResponses(Long couponId, Pageable pageable) {
		AuditReader reader = AuditReaderFactory.get(entityManager);

		List<Object[]> couponRevisions = reader.createQuery().forRevisionsOfEntity(Coupon.class, false, true)
			.add(AuditEntity.id().eq(couponId))
			.addOrder(AuditEntity.revisionNumber().desc())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		Long count = (Long)reader.createQuery()
			.forRevisionsOfEntity(Coupon.class, true, true)
			.add(AuditEntity.id().eq(couponId))
			.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();

		List<CouponRevisionResponseDto> couponRevisionResponseDtos = couponRevisions.stream()
			.map(couponRevision -> {
				RevisionType revisionType = (RevisionType)couponRevision[2];
				return CouponRevisionResponseDto.of(
					revisionType.getRepresentation(),
					(Coupon)couponRevision[0]
				);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(couponRevisionResponseDtos, pageable, count);
	}
}
