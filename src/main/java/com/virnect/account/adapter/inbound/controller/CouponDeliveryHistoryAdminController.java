package com.virnect.account.adapter.inbound.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.coupondeliveryhistory.CouponDeliveryHistorySearchDto;
import com.virnect.account.adapter.inbound.dto.response.CouponDeliveryHistoryResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.CouponDeliveryHistoryService;

@Api
@Validated
@Tag(name = "Coupon Delivery History Admin", description = "Coupon 메일 전송 이력에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupon-delivery-histories")
public class CouponDeliveryHistoryAdminController {
	private final String COUPON_DELIVERY_HISTORY_ADMIN_TAG = "Coupon Delivery History Admin";

	private final CouponDeliveryHistoryService couponDeliveryHistoryService;

	@Operation(summary = "Coupon 메일 전송 이력 조회", description = "Admin 사용자의 Coupon 메일 전송 이력 조회 API", tags = COUPON_DELIVERY_HISTORY_ADMIN_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<CouponDeliveryHistoryResponseDto>> getCouponDeliveryHistories(
		@ModelAttribute CouponDeliveryHistorySearchDto couponDeliveryHistorySearchDto, PageRequest pageRequest
	) {
		PageContentResponseDto<CouponDeliveryHistoryResponseDto> couponDeliveryHistoryResponseDto = couponDeliveryHistoryService.getCouponDeliveryHistories(
			couponDeliveryHistorySearchDto, pageRequest.of());
		return new ResponseEntity<>(couponDeliveryHistoryResponseDto, HttpStatus.OK);
	}
}
