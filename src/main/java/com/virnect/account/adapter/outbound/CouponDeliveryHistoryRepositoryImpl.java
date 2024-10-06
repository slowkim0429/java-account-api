package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QCouponDeliveryHistory.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.coupondeliveryhistory.CouponDeliveryHistorySearchDto;
import com.virnect.account.adapter.inbound.dto.response.CouponDeliveryHistoryResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QCouponDeliveryHistoryResponseDto;
import com.virnect.account.port.outbound.CouponDeliveryHistoryRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class CouponDeliveryHistoryRepositoryImpl implements CouponDeliveryHistoryRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<CouponDeliveryHistoryResponseDto> getCouponDeliveryHistoryResponses(
		CouponDeliveryHistorySearchDto couponDeliveryHistorySearchDto, Pageable pageable
	) {
		QueryResults<CouponDeliveryHistoryResponseDto> pageResult = queryFactory
			.select(new QCouponDeliveryHistoryResponseDto(
				couponDeliveryHistory.id,
				couponDeliveryHistory.couponId,
				couponDeliveryHistory.eventPopupId,
				couponDeliveryHistory.receiverUserId,
				couponDeliveryHistory.receiverEmail,
				couponDeliveryHistory.receiverEmailDomain,
				couponDeliveryHistory.lastModifiedBy,
				couponDeliveryHistory.updatedDate,
				couponDeliveryHistory.createdBy,
				couponDeliveryHistory.createdDate
			))
			.from(couponDeliveryHistory)
			.where(
				eqEventPopupId(couponDeliveryHistorySearchDto.getEventPopupId()),
				eqCouponId(couponDeliveryHistorySearchDto.getCouponId()),
				eqReceiverUserId(couponDeliveryHistorySearchDto.getReceiverUserId()),
				startsWithReceiverEmail(couponDeliveryHistorySearchDto.getReceiverEmail()),
				startsWithReceiverEmailDomain(couponDeliveryHistorySearchDto.getReceiverEmailDomain())
			)
			.orderBy(couponDeliveryHistory.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();
		return new PageImpl<>(pageResult.getResults(), pageable, pageResult.getTotal());
	}

	private BooleanExpression eqEventPopupId(Long eventPopupId) {
		if (eventPopupId == null || eventPopupId <= 0) {
			return null;
		}
		return couponDeliveryHistory.eventPopupId.eq(eventPopupId);
	}

	private BooleanExpression eqCouponId(Long couponId) {
		if (couponId == null || couponId <= 0) {
			return null;
		}
		return couponDeliveryHistory.couponId.eq(couponId);
	}

	private BooleanExpression eqReceiverUserId(Long receiverUserId) {
		if (receiverUserId == null || receiverUserId <= 0) {
			return null;
		}
		return couponDeliveryHistory.receiverUserId.eq(receiverUserId);
	}

	private BooleanExpression startsWithReceiverEmail(String receiverEmail) {
		if (StringUtils.isBlank(receiverEmail)) {
			return null;
		}
		return couponDeliveryHistory.receiverEmail.startsWith(receiverEmail);
	}

	private BooleanExpression startsWithReceiverEmailDomain(String receiverEmailDomain) {
		if (StringUtils.isBlank(receiverEmailDomain)) {
			return null;
		}
		return couponDeliveryHistory.receiverEmailDomain.startsWith(receiverEmailDomain);
	}
}
