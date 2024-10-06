package com.virnect.account.port.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.coupondeliveryhistory.CouponDeliveryHistorySearchDto;
import com.virnect.account.adapter.inbound.dto.response.CouponDeliveryHistoryResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.CouponDeliveryHistoryService;
import com.virnect.account.port.outbound.CouponDeliveryHistoryRepository;

@Service
@RequiredArgsConstructor
public class CouponDeliveryHistoryServiceImpl implements CouponDeliveryHistoryService {
	private final CouponDeliveryHistoryRepository couponDeliveryHistoryRepository;

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<CouponDeliveryHistoryResponseDto> getCouponDeliveryHistories(
		CouponDeliveryHistorySearchDto couponDeliveryHistorySearchDto, Pageable pageable
	) {
		Page<CouponDeliveryHistoryResponseDto> couponDeliveryHistoryResponses = couponDeliveryHistoryRepository.getCouponDeliveryHistoryResponses(
			couponDeliveryHistorySearchDto, pageable);
		return new PageContentResponseDto<>(couponDeliveryHistoryResponses, pageable);
	}
}
