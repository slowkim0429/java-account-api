package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.ApprovalStatusRequestDto;
import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.UseStatusRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponModifyRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponSearchDto;
import com.virnect.account.adapter.inbound.dto.response.CouponResponseDto;
import com.virnect.account.adapter.inbound.dto.response.CouponRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.CouponService;

@Api
@Validated
@Tag(name = "Coupon Admin", description = "Coupon에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons")
public class CouponAdminController {
	private final CouponService couponService;

	@Operation(summary = "Coupon 목록 조회", description = "Admin 사용자의 Coupon 목록 조회", tags = {"Coupon Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<CouponResponseDto>> getCoupons(
		@ModelAttribute @Valid CouponSearchDto couponSearchDto, PageRequest pageRequest
	) {
		PageContentResponseDto<CouponResponseDto> contentResponseDto = couponService.getCoupons(
			couponSearchDto, pageRequest.of());
		return ResponseEntity.ok(contentResponseDto);
	}

	@Operation(summary = "Coupon 변경 이력 조회", description = "Admin 사용자의 Coupon 변경 이력 조회", tags = {"Coupon Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{couponId}/revisions")
	public ResponseEntity<PageContentResponseDto<CouponRevisionResponseDto>> getCouponRevisions(
		@Min(1000000000) @PathVariable("couponId") Long couponId, PageRequest pageRequest) {
		PageContentResponseDto<CouponRevisionResponseDto> couponRevisions = couponService.getCouponRevisions(
			couponId, pageRequest.of());
		return new ResponseEntity<>(couponRevisions, HttpStatus.OK);
	}

	@Operation(summary = "Coupon 생성", description = "Admin 사용자의 Coupon 생성 요청", tags = {"Coupon Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping
	public ResponseEntity<Void> create(@RequestBody @Valid CouponCreateRequestDto couponCreateRequestDto) {
		if (!couponCreateRequestDto.isExpiredDateValid()) {
			throw new CustomException(ErrorCode.INVALID_EXPIRED_DATE, couponCreateRequestDto.getExpiredDateInvalidMessage());
		}

		if (!couponCreateRequestDto.isBenefitOptionValid()) {
			throw new CustomException(ErrorCode.INVALID_BENEFIT_OPTION, couponCreateRequestDto.getBenefitOptionInvalidMessage());
		}

		couponService.create(couponCreateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Coupon 승인 상태 변경", description = "Admin 사용자의 Coupon 승인 상태 변경", tags = "Coupon Admin")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{couponId}/approval-status")
	public ResponseEntity<Void> updateApprovalStatus(
		@Min(1000000000) @PathVariable Long couponId,
		@RequestBody @Valid ApprovalStatusRequestDto approvalStatusRequestDto
	) {
		couponService.updateApprovalStatus(couponId, approvalStatusRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Coupon 사용 상태 변경", description = "Admin 사용자의 Coupon 사용 상태 변경", tags = "Coupon Admin")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{couponId}/use-status")
	public ResponseEntity<Void> updateUseStatus(
		@Min(1000000000) @PathVariable Long couponId,
		@RequestBody @Valid UseStatusRequestDto useStatusRequestDto
	) {
		couponService.updateUseStatus(couponId, useStatusRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Coupon 데이터 수정", description = "Admin 사용자의 Coupon 데이터 수정 요청", tags = {"Coupon Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{couponId}")
	public ResponseEntity<Void> modify(
		@Min(1000000000) @PathVariable Long couponId,
		@RequestBody @Valid CouponModifyRequestDto couponModifyRequestDto) {
		if (!couponModifyRequestDto.isExpiredDateValid()) {
			throw new CustomException(ErrorCode.INVALID_EXPIRED_DATE, couponModifyRequestDto.getExpiredDateInvalidMessage());
		}

		if (!couponModifyRequestDto.isBenefitOptionValid()) {
			throw new CustomException(ErrorCode.INVALID_BENEFIT_OPTION, couponModifyRequestDto.getBenefitOptionInvalidMessage());
		}

		couponService.modify(couponId, couponModifyRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Coupon 상세 조회", description = "Admin 사용자의 Coupon 상세 조회 요청", tags = {"Coupon Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{couponId}")
	public ResponseEntity<CouponResponseDto> getCoupon(@Min(1000000000) @PathVariable Long couponId) {
		CouponResponseDto couponResponseDto = couponService.getCoupon(couponId);
		return ResponseEntity.ok(couponResponseDto);
	}

	@Operation(summary = "Synchronize coupon data", description = "타 서버로 coupon 데이터 동기화", tags = {"Coupon Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping("/{couponId}/synchronize")
	public ResponseEntity<Void> synchronizeCoupon(@Min(1000000000) @PathVariable Long couponId) {
		couponService.synchronizeCoupon(couponId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
