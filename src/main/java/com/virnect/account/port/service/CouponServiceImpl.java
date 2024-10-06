package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.ApprovalStatusRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UseStatusRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponModifyRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponSearchDto;
import com.virnect.account.adapter.inbound.dto.response.CouponResponseDto;
import com.virnect.account.adapter.inbound.dto.response.CouponRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.outbound.request.CouponSendDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CouponBenefitOption;
import com.virnect.account.domain.enumclass.CouponType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Coupon;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.ContractAPIService;
import com.virnect.account.port.inbound.CouponService;
import com.virnect.account.port.outbound.CouponRepository;
import com.virnect.account.port.outbound.CouponRevisionRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
	private final CouponRepository couponRepository;
	private final CouponRevisionRepository couponRevisionRepository;

	private final ContractAPIService contractAPIService;

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<CouponResponseDto> getCoupons(CouponSearchDto couponSearchDto, Pageable pageable) {
		Page<CouponResponseDto> couponResponseDtos = couponRepository.getCouponResponses(couponSearchDto, pageable);
		return new PageContentResponseDto<>(couponResponseDtos, pageable);
	}

	@Override
	public void create(CouponCreateRequestDto couponCreateRequestDto) {
		if (CouponType.UPGRADE_LICENSE_PERIOD.equals(couponCreateRequestDto.couponTypeValueOf())) {
			checkPeriodBenefitValue(
				couponCreateRequestDto.benefitOptionValueOf(), couponCreateRequestDto.getBenefitValue());
		}

		checkDuplicateCouponCode(couponCreateRequestDto.getCode());

		final Coupon coupon = Coupon.create(couponCreateRequestDto);
		couponRepository.save(coupon);
	}

	private void checkPeriodBenefitValue(CouponBenefitOption benefitOption, Long benefitValue) {
		if (CouponBenefitOption.YEAR.equals(benefitOption) && benefitValue >= 10) {
			throw new CustomException(ErrorCode.INVALID_PERIOD_BENEFIT_VALUE);
		}

		if (CouponBenefitOption.MONTH.equals(benefitOption) && benefitValue >= 120) {
			throw new CustomException(ErrorCode.INVALID_PERIOD_BENEFIT_VALUE);
		}
	}

	@Override
	public void modify(Long couponId, CouponModifyRequestDto couponModifyRequestDto) {
		if (CouponType.UPGRADE_LICENSE_PERIOD.equals(couponModifyRequestDto.couponTypeValueOf())) {
			checkPeriodBenefitValue(
				couponModifyRequestDto.benefitOptionValueOf(), couponModifyRequestDto.getBenefitValue());
		}

		Coupon coupon = couponRepository.getCoupon(couponId, null)
			.orElseThrow(() -> new CustomException(NOT_FOUND_COUPON));

		if (coupon.getStatus().isNotRegister()) {
			throw new CustomException(INVALID_STATUS);
		}

		if(!coupon.getCode().equals(couponModifyRequestDto.getCode())) {
			checkDuplicateCouponCode(couponModifyRequestDto.getCode());
		}

		coupon.update(couponModifyRequestDto);
	}

	@Override
	public void updateApprovalStatus(Long couponId, ApprovalStatusRequestDto approvalStatusRequestDto) {
		Coupon coupon = couponRepository.getCoupon(couponId, null)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COUPON));

		if (coupon.getStatus().isImmutableStatus()) {
			throw new CustomException(INVALID_STATUS);
		}

		ApprovalStatus status = approvalStatusRequestDto.statusValueOf();
		coupon.updateStatus(status);

		if (status.isApproved()) {
			synchronizeCouponToContract(coupon);
		}
	}

	@Override
	public void updateUseStatus(Long couponId, UseStatusRequestDto useStatusRequestDto) {
		Coupon coupon = couponRepository.getCoupon(couponId, null)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COUPON));

		if (coupon.getStatus().isNotApproved()) {
			throw new CustomException(INVALID_STATUS);
		}

		UseStatus useStatus = useStatusRequestDto.useStatusValueOf();
		coupon.updateUseStatus(useStatus);

		synchronizeCouponToContract(coupon);
	}

	@Override
	@Transactional(readOnly = true)
	public CouponResponseDto getCoupon(Long couponId) {
		return couponRepository.getCouponResponse(couponId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_COUPON));
	}

	@Override
	public void synchronizeCoupon(Long couponId) {
		Coupon coupon = couponRepository.getCoupon(couponId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COUPON));

		if (coupon.getStatus().isNotApproved()) {
			throw new CustomException(INVALID_STATUS);
		}

		synchronizeCouponToContract(coupon);
	}

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<CouponRevisionResponseDto> getCouponRevisions(Long couponId, Pageable pageable) {
		Page<CouponRevisionResponseDto> couponRevisions = couponRevisionRepository.getCouponRevisionResponses(
			couponId, pageable);
		return new PageContentResponseDto<>(couponRevisions, pageable);
	}

	private void synchronizeCouponToContract(Coupon coupon) {
		CouponSendDto couponSendDto = CouponSendDto.of(coupon);
		contractAPIService.syncCoupon(couponSendDto);
	}

	private void checkDuplicateCouponCode(String code) {
		couponRepository.getCoupon(code)
			.ifPresent(coupon -> {
				throw new CustomException(DUPLICATE_COUPON_CODE);
			});
	}
}
