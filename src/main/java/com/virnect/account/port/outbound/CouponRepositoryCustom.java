package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.coupon.CouponSearchDto;
import com.virnect.account.adapter.inbound.dto.response.CouponResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.model.Coupon;

public interface CouponRepositoryCustom {
	Page<CouponResponseDto> getCouponResponses(CouponSearchDto couponSearchDto, Pageable pageable);

	Optional<CouponResponseDto> getCouponResponse(Long couponId);

	Optional<Coupon> getCoupon(Long couponId, ApprovalStatus status, String code);

	Optional<Coupon> getCoupon(Long couponId, ApprovalStatus status);

	Optional<Coupon> getCoupon(Long couponId);

	Optional<Coupon> getCoupon(String code);
}
