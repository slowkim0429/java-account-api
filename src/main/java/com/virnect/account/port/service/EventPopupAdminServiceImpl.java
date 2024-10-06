package com.virnect.account.port.service;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupSearchDto;
import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.CouponResponseDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.FileDirectory;
import com.virnect.account.domain.model.EventPopup;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.CouponService;
import com.virnect.account.port.inbound.EventPopupAdminService;
import com.virnect.account.port.inbound.FileService;
import com.virnect.account.port.outbound.EventPopupRepository;
import com.virnect.account.port.outbound.EventPopupRevisionRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class EventPopupAdminServiceImpl implements EventPopupAdminService {
	private final EventPopupRepository eventPopupRepository;
	private final EventPopupRevisionRepository eventPopupRevisionRepository;
	private final FileService fileService;
	private final CouponService couponService;

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<EventPopupAdminResponseDto> getEventPopups(
		EventPopupSearchDto eventPopupSearchDto, Pageable pageable
	) {
		Page<EventPopupAdminResponseDto> eventPopupResponses = eventPopupRepository.getEventPopupResponses(
			eventPopupSearchDto, pageable
		);
		return new PageContentResponseDto<>(eventPopupResponses, pageable);
	}

	@Override
	public void create(EventPopupCreateRequestDto eventPopupCreateRequestDto) {
		checkCouponId(eventPopupCreateRequestDto.getCouponId());
		String imageUrl = fileService.upload(eventPopupCreateRequestDto.getImage(), FileDirectory.EVENT_POPUP);
		String emailContentInlineImageUrl = "";
		if (eventPopupCreateRequestDto.hasEmailContentInlineImage()) {
			emailContentInlineImageUrl = fileService.upload(
				eventPopupCreateRequestDto.getEmailContentInlineImage(), FileDirectory.EVENT_POPUP);
		}
		EventPopup eventPopup = EventPopup.of(eventPopupCreateRequestDto, imageUrl, emailContentInlineImageUrl);
		eventPopupRepository.save(eventPopup);
	}

	public void checkCouponId(Long couponId) {
		if (Objects.isNull(couponId))
			return;

		CouponResponseDto coupon = couponService.getCoupon(couponId);
		if (Objects.isNull(coupon)) {
			throw new CustomException(ErrorCode.NOT_FOUND_EVENT_POPUP_COUPON);
		}
		if (coupon.getStatus().isNotApproved()) {
			throw new CustomException(ErrorCode.INVALID_EVENT_POPUP_COUPON_NOT_APPROVED);
		}
		if (coupon.getUseStatus().isNotUse()) {
			throw new CustomException(ErrorCode.INVALID_EVENT_POPUP_COUPON_NOT_USED);
		}
	}

	@Override
	public void changeExpose(Long eventPopupId, Boolean isExposed) {
		if (isExposed) {
			EventPopup exposedEventPopup = eventPopupRepository.getEventPopup(true).orElse(null);
			if (Objects.nonNull(exposedEventPopup)) {
				if (eventPopupId.equals(exposedEventPopup.getId())) {
					return;
				} else {
					exposedEventPopup.setIsExposed(false);
				}
			}
		}
		eventPopupRepository.getEventPopup(eventPopupId)
			.ifPresent(eventPopup -> eventPopup.setIsExposed(isExposed));
	}

	@Override
	public void update(Long eventPopupId, EventPopupUpdateRequestDto eventPopupUpdateRequestDto) {
		EventPopup eventPopup = eventPopupRepository.getEventPopup(eventPopupId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EVENT_POPUP));
		checkValidation(eventPopup, eventPopupUpdateRequestDto);
		if (eventPopupUpdateRequestDto.getImage() != null) {
			String uploadImageUrl = fileService.replaceFileUrl(
				eventPopup.getImageUrl(),
				eventPopupUpdateRequestDto.getImage(),
				FileDirectory.EVENT_POPUP
			);
			eventPopup.setImageUrl(uploadImageUrl);
		}
		if (eventPopupUpdateRequestDto.getEmailContentInlineImage() != null) {
			String uploadEmailContentInlineImageUrl = fileService.replaceFileUrl(
				eventPopup.getEmailContentInlineImageUrl(),
				eventPopupUpdateRequestDto.getEmailContentInlineImage(),
				FileDirectory.EVENT_POPUP
			);
			eventPopup.setEmailContentInlineImageUrl(uploadEmailContentInlineImageUrl);
		}
		eventPopup.update(eventPopupUpdateRequestDto);
	}

	private void checkValidation(EventPopup eventPopup, EventPopupUpdateRequestDto eventPopupUpdateRequestDto) {
		if (eventPopup.getEventType().isMarketing()) {
			if (StringUtils.isNotBlank(eventPopupUpdateRequestDto.getInputGuide())) {
				throw new CustomException(ErrorCode.INVALID_EVENT_POPUP_MODIFY);
			}
			if (StringUtils.isNotBlank(eventPopupUpdateRequestDto.getEmailTitle())) {
				throw new CustomException(ErrorCode.INVALID_EVENT_POPUP_MODIFY);
			}
		} else if (eventPopup.getEventType().isSubmission()) {
			if (StringUtils.isNotBlank(eventPopupUpdateRequestDto.getButtonUrl())) {
				throw new CustomException(ErrorCode.INVALID_EVENT_POPUP_MODIFY);
			}
		}
	}

	@Transactional(readOnly = true)
	@Override
	public EventPopupAdminResponseDto get(Long eventPopupId) {
		return eventPopupRepository.getEventPopupAdminResponse(eventPopupId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EVENT_POPUP));
	}

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<EventPopupRevisionResponseDto> getEventPopupRevisions(
		Long eventPopupId, Pageable pageable
	) {
		Page<EventPopupRevisionResponseDto> eventPopupRevisionResponses = eventPopupRevisionRepository.getEventPopupRevisionResponses(
			eventPopupId, pageable);
		return new PageContentResponseDto<>(eventPopupRevisionResponses, pageable);
	}
}
