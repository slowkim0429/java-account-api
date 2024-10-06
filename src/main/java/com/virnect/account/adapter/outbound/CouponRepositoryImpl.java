package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QCoupon.*;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.coupon.CouponSearchDto;
import com.virnect.account.adapter.inbound.dto.response.CouponResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QCouponResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CouponType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Coupon;
import com.virnect.account.port.outbound.CouponRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<CouponResponseDto> getCouponResponses(CouponSearchDto couponSearchDto, Pageable pageable) {
		QueryResults<CouponResponseDto> pageResult = queryFactory
			.select(new QCouponResponseDto(
				coupon.id,
				coupon.code,
				coupon.name,
				coupon.description,
				coupon.couponType,
				coupon.benefitOption,
				coupon.benefitValue,
				coupon.couponLicenseGradeMatchingType,
				coupon.couponRecurringIntervalMatchingType,
				coupon.maxCount,
				coupon.expiredDate,
				coupon.status,
				coupon.useStatus,
				coupon.createdDate,
				coupon.createdBy,
				coupon.updatedDate,
				coupon.lastModifiedBy
			))
			.from(coupon)
			.where(
				eqCouponType(couponSearchDto.couponTypeValueOf()),
				eqStatus(couponSearchDto.statusValueOf()),
				eqUseStatus(couponSearchDto.useStatusValueOf()),
				startsWithName(couponSearchDto.getName())
			)
			.orderBy(coupon.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(pageResult.getResults(), pageable, pageResult.getTotal());
	}

	@Override
	public Optional<CouponResponseDto> getCouponResponse(Long couponId) {
		return Optional.ofNullable(
			queryFactory
				.select(new QCouponResponseDto(
					coupon.id,
					coupon.code,
					coupon.name,
					coupon.description,
					coupon.couponType,
					coupon.benefitOption,
					coupon.benefitValue,
					coupon.couponLicenseGradeMatchingType,
					coupon.couponRecurringIntervalMatchingType,
					coupon.maxCount,
					coupon.expiredDate,
					coupon.status,
					coupon.useStatus,
					coupon.createdDate,
					coupon.createdBy,
					coupon.updatedDate,
					coupon.lastModifiedBy
				))
				.from(coupon)
				.where(
					coupon.id.eq(couponId)
				)
				.fetchOne());
	}

	@Override
	public Optional<Coupon> getCoupon(Long couponId, ApprovalStatus status, String code) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(coupon)
				.where(
					eqCouponId(couponId),
					eqStatus(status),
					eqCode(code)
				)
				.fetchOne()
		);
	}

	@Override
	public Optional<Coupon> getCoupon(Long couponId, ApprovalStatus status) {
		return getCoupon(couponId, status, null);
	}

	@Override
	public Optional<Coupon> getCoupon(Long couponId) {
		return getCoupon(couponId, null);
	}

	@Override
	public Optional<Coupon> getCoupon(String code) {
		return getCoupon(null, null, code);
	}

	private BooleanExpression startsWithName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		return coupon.name.startsWith(name);
	}

	private BooleanExpression eqUseStatus(UseStatus useStatus) {
		if (useStatus == null) {
			return null;
		}
		return coupon.useStatus.eq(useStatus);
	}

	private BooleanExpression eqStatus(ApprovalStatus status) {
		if (status == null) {
			return null;
		}
		return coupon.status.eq(status);
	}

	private BooleanExpression eqCouponType(CouponType couponType) {
		if (couponType == null) {
			return null;
		}
		return coupon.couponType.eq(couponType);
	}

	private BooleanExpression eqCouponId(Long couponId) {
		if (couponId == null) {
			return null;
		}
		return coupon.id.eq(couponId);
	}

	private BooleanExpression eqCode(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		return coupon.code.eq(code);
	}
}
