package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.coupondeliveryhistory.CouponDeliveryHistorySearchDto;
import com.virnect.account.adapter.inbound.dto.response.CouponDeliveryHistoryResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;

public interface CouponDeliveryHistoryService {
	PageContentResponseDto<CouponDeliveryHistoryResponseDto> getCouponDeliveryHistories(
		CouponDeliveryHistorySearchDto couponDeliveryHistorySearchDto, Pageable pageable
	);
}
