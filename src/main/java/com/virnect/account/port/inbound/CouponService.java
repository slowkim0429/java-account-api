package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.ApprovalStatusRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UseStatusRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponModifyRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponSearchDto;
import com.virnect.account.adapter.inbound.dto.response.CouponResponseDto;
import com.virnect.account.adapter.inbound.dto.response.CouponRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;

public interface CouponService {
	PageContentResponseDto<CouponResponseDto> getCoupons(CouponSearchDto couponSearchDto, Pageable pageable);

	void create(CouponCreateRequestDto couponCreateRequestDto);

	void updateApprovalStatus(Long couponId, ApprovalStatusRequestDto approvalStatusRequestDto);

	void updateUseStatus(Long couponId, UseStatusRequestDto useStatusRequestDto);

	void modify(Long couponId, CouponModifyRequestDto couponModifyRequestDto);

	CouponResponseDto getCoupon(Long couponId);

	void synchronizeCoupon(Long couponId);

	PageContentResponseDto<CouponRevisionResponseDto> getCouponRevisions(Long couponId, Pageable pageable);
}
