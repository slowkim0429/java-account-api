package com.virnect.account.port.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.eventpopup.SendCouponRequestDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;
import com.virnect.account.domain.model.Coupon;
import com.virnect.account.domain.model.CouponDeliveryHistory;
import com.virnect.account.domain.model.EventPopup;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.EmailAuthService;
import com.virnect.account.port.inbound.EventPopupService;
import com.virnect.account.port.outbound.CouponDeliveryHistoryRepository;
import com.virnect.account.port.outbound.CouponRepository;
import com.virnect.account.port.outbound.EventPopupRepository;
import com.virnect.account.port.outbound.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class EventPopupServiceImpl implements EventPopupService {
	private final EmailAuthService emailAuthService;
	private final EventPopupRepository eventPopupRepository;
	private final CouponRepository couponRepository;
	private final CouponDeliveryHistoryRepository couponDeliveryHistoryRepository;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	@Override
	public EventPopupResponseDto getLatestEventPopup(EventType eventType, EventServiceType serviceType) {
		return eventPopupRepository.getEventPopupResponse(eventType, serviceType, true).orElse(null);
	}

	@Override
	public void sendCouponEmail(Long eventPopupId, SendCouponRequestDto sendCouponRequestDto) {
		EventPopup eventPopup = eventPopupRepository.getEventPopup(eventPopupId, EventType.SUBMISSION, true)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EVENT_POPUP));

		Coupon coupon = couponRepository.getCoupon(eventPopup.getCouponId(), ApprovalStatus.APPROVED)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COUPON));

		emailAuthService.sendCouponEmail(
			eventPopup.getEmailTitle(), eventPopup.getEmailContentInlineImageUrl(), coupon.getCode(),
			coupon.getCouponLicenseGradeMatchingType().name(), coupon.getCouponRecurringIntervalMatchingType().name(),
			coupon.getExpiredDate(), coupon.getBenefitOption(), coupon.getBenefitValue(), sendCouponRequestDto.getEmail()
		);

		userRepository.getUser(sendCouponRequestDto.getEmail())
			.ifPresentOrElse(
				user -> {
					CouponDeliveryHistory couponDeliveryHistory = CouponDeliveryHistory.create(
						coupon.getId(), eventPopup.getId(), user.getId(), sendCouponRequestDto.getEmail());
					couponDeliveryHistoryRepository.save(couponDeliveryHistory);
				},
				() -> {
					CouponDeliveryHistory couponDeliveryHistory = CouponDeliveryHistory.create(
						coupon.getId(), eventPopup.getId(), null, sendCouponRequestDto.getEmail());
					couponDeliveryHistoryRepository.save(couponDeliveryHistory);
				}
			);
	}
}
