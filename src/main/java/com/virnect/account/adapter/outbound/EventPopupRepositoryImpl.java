package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QEventPopup.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupSearchDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QEventPopupAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QEventPopupResponseDto;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;
import com.virnect.account.domain.model.EventPopup;
import com.virnect.account.port.outbound.EventPopupRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class EventPopupRepositoryImpl implements EventPopupRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<EventPopupResponseDto> getEventPopupResponse(
		EventType eventType, EventServiceType serviceType, Boolean isExposed
	) {
		return Optional.ofNullable(queryFactory.select(new QEventPopupResponseDto(
				eventPopup.id,
				eventPopup.name,
				eventPopup.eventType,
				eventPopup.serviceType,
				eventPopup.imageUrl,
				eventPopup.imageLinkUrl,
				eventPopup.contentDescription,
				eventPopup.buttonLabel,
				eventPopup.buttonUrl,
				eventPopup.exposureOptionType,
				eventPopup.exposureOptionDataType,
				eventPopup.exposureOptionValue,
				eventPopup.isExposed,
				eventPopup.couponId,
				eventPopup.inputGuide,
				eventPopup.emailTitle,
				eventPopup.emailContentInlineImageUrl
			)).from(eventPopup)
			.where(
				eventPopup.eventType.eq(eventType),
				eventPopup.serviceType.eq(serviceType),
				eventPopup.isExposed.eq(isExposed)
			)
			.orderBy(eventPopup.id.desc())
			.fetchFirst());
	}

	@Override
	public Page<EventPopupAdminResponseDto> getEventPopupResponses(
		EventPopupSearchDto eventPopupSearchDto, Pageable pageable
	) {
		QueryResults<EventPopupAdminResponseDto> results = queryFactory.select(
				new QEventPopupAdminResponseDto(
					eventPopup.id,
					eventPopup.name,
					eventPopup.eventType,
					eventPopup.serviceType,
					eventPopup.imageUrl,
					eventPopup.imageLinkUrl,
					eventPopup.contentDescription,
					eventPopup.buttonLabel,
					eventPopup.buttonUrl,
					eventPopup.exposureOptionType,
					eventPopup.exposureOptionDataType,
					eventPopup.exposureOptionValue,
					eventPopup.isExposed,
					eventPopup.couponId,
					eventPopup.inputGuide,
					eventPopup.emailTitle,
					eventPopup.emailContentInlineImageUrl,
					eventPopup.createdBy,
					eventPopup.createdDate,
					eventPopup.lastModifiedBy,
					eventPopup.updatedDate
				)).from(eventPopup)
			.where(
				eqId(eventPopupSearchDto.getId()),
				eqCouponId(eventPopupSearchDto.getCouponId()),
				startWithName(eventPopupSearchDto.getName()),
				eqEventType(EventType.nullableValueOf(eventPopupSearchDto.getEventType())),
				eqIsExposed(eventPopupSearchDto.getIsExposed()),
				eqServiceType(EventServiceType.nullableValueOf(eventPopupSearchDto.getServiceType()))
			)
			.orderBy(eventPopup.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();
		return new PageImpl<>(results.getResults(), pageable, results.getTotal());
	}

	public BooleanExpression eqId(Long id) {
		if (id == null) {
			return null;
		}
		return eventPopup.id.eq(id);
	}

	public BooleanExpression eqIsExposed(Boolean isExposed) {
		if (isExposed == null) {
			return null;
		}
		return eventPopup.isExposed.eq(isExposed);
	}

	public BooleanExpression eqCouponId(Long couponId) {
		if (couponId == null) {
			return null;
		}
		return eventPopup.couponId.eq(couponId);
	}

	public BooleanExpression startWithName(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		return eventPopup.name.startsWith(name);
	}

	@Override
	public Optional<EventPopup> getEventPopup(Long eventPopupId, EventType eventType, Boolean isExposed) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(eventPopup)
				.where(
					eqId(eventPopupId),
					eqEventType(eventType),
					eqIsExposed(isExposed)
				)
				.fetchFirst()
		);
	}

	@Override
	public Optional<EventPopup> getEventPopup(Long eventPopupId) {
		return getEventPopup(eventPopupId, null, null);
	}

	@Override
	public Optional<EventPopup> getEventPopup(Boolean isExposed) {
		return getEventPopup(null, null, isExposed);
	}

	public BooleanExpression eqEventType(EventType eventType) {
		if (eventType == null) {
			return null;
		}
		return eventPopup.eventType.eq(eventType);
	}

	public BooleanExpression eqServiceType(EventServiceType serviceType) {
		if (serviceType == null) {
			return null;
		}
		return eventPopup.serviceType.eq(serviceType);
	}

	@Override
	public Optional<EventPopupAdminResponseDto> getEventPopupAdminResponse(Long eventPopupId) {
		return Optional.ofNullable(
			queryFactory
				.select(new QEventPopupAdminResponseDto(
					eventPopup.id,
					eventPopup.name,
					eventPopup.eventType,
					eventPopup.serviceType,
					eventPopup.imageUrl,
					eventPopup.imageLinkUrl,
					eventPopup.contentDescription,
					eventPopup.buttonLabel,
					eventPopup.buttonUrl,
					eventPopup.exposureOptionType,
					eventPopup.exposureOptionDataType,
					eventPopup.exposureOptionValue,
					eventPopup.isExposed,
					eventPopup.couponId,
					eventPopup.inputGuide,
					eventPopup.emailTitle,
					eventPopup.emailContentInlineImageUrl,
					eventPopup.createdBy,
					eventPopup.createdDate,
					eventPopup.lastModifiedBy,
					eventPopup.updatedDate
				))
				.from(eventPopup)
				.where(
					eventPopup.id.eq(eventPopupId)
				)
				.fetchOne()
		);
	}
}
