package com.virnect.account.port.outbound;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.coupondeliveryhistory.CouponDeliveryHistorySearchDto;
import com.virnect.account.adapter.inbound.dto.response.CouponDeliveryHistoryResponseDto;

public interface CouponDeliveryHistoryRepositoryCustom {
	Page<CouponDeliveryHistoryResponseDto> getCouponDeliveryHistoryResponses(
		CouponDeliveryHistorySearchDto couponDeliveryHistorySearchDto, Pageable pageable
	);
}
