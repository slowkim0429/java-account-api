package com.virnect.account.adapter.inbound.controller;

import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.eventpopup.SendCouponRequestDto;
import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.adapter.inbound.dto.response.EventPopupResponseDto;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;
import com.virnect.account.port.inbound.EventPopupService;

@Api
@Validated
@Tag(name = "Event popup", description = "Event popup 에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event-popups")
public class EventPopupController {

	private final EventPopupService eventPopupService;

	@Operation(summary = "Event popup 노출 글 조회", description = "사용자가 노출 설정 된 Event popup 을 조회", tags = {"Event popup"})
	@PreAuthorize("permitAll()")
	@GetMapping("/event-types/{eventType}/service-types/{serviceType}/latest")
	public ResponseEntity<EventPopupResponseDto> getLatestEventPopup(
		@PathVariable @CommonEnum(enumClass = EventType.class) String eventType,
		@PathVariable @CommonEnum(enumClass = EventServiceType.class) String serviceType
	) {
		EventPopupResponseDto exposedEventPopup = eventPopupService.getLatestEventPopup(
			EventType.valueOf(eventType),
			EventServiceType.valueOf(serviceType)
		);
		return ResponseEntity.ok(exposedEventPopup);
	}

	@Operation(summary = "쿠폰 메일 발송", description = "쿠폰 메일 발송", tags = {"Event popup"})
	@PreAuthorize("permitAll()")
	@PostMapping("/{eventPopupId}/coupons/email")
	public ResponseEntity<Void> sendCouponEmail(
		@Min(1000000000) @PathVariable("eventPopupId") Long eventPopupId,
		@RequestBody SendCouponRequestDto sendCouponRequestDto
	) {
		eventPopupService.sendCouponEmail(eventPopupId, sendCouponRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
