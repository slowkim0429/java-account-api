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

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupExposeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupSearchDto;
import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.EventPopupAdminService;

@Api
@Validated
@Tag(name = "Event popup Admin", description = "Admin 전용 Event popup api")
@RestController
@RequestMapping("/api/admin/event-popups")
@RequiredArgsConstructor
public class EventPopupAdminController {

	private final EventPopupAdminService eventPopupAdminService;

	@Operation(summary = "Event popup 조회", description = "Admin 이 Event popup 을 조회합니다", tags = {"Event popup Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<EventPopupAdminResponseDto>> getEventPopups(
		@ModelAttribute @Valid EventPopupSearchDto eventPopupSearchDto, PageRequest pageRequest
	) {
		PageContentResponseDto<EventPopupAdminResponseDto> eventPopupResponses = eventPopupAdminService.getEventPopups(
			eventPopupSearchDto, pageRequest.of());
		return ResponseEntity.ok(eventPopupResponses);
	}

	@Operation(summary = "Event popup 생성", description = "Admin 이 Event popup 을 생성 합니다.", tags = {"Event popup Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping
	public ResponseEntity<Void> create(@Valid @ModelAttribute EventPopupCreateRequestDto eventPopupCreateRequestDto) {
		eventPopupAdminService.create(eventPopupCreateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(
		summary = "Event popup 노출 여부 변경",
		description = "Admin 이 Event popup 의 노출 설정을 변경합니다.",
		tags = {"Event popup Admin"}
	)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{eventPopupId}/expose")
	public ResponseEntity<Void> changeExpose(
		@Min(1000000000) @PathVariable Long eventPopupId,
		@Valid @RequestBody EventPopupExposeRequestDto eventPopupExposeRequestDto
	) {
		eventPopupAdminService.changeExpose(eventPopupId, eventPopupExposeRequestDto.getIsExposed());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Event popup 수정", description = "Admin 이 Event popup 을 수정 합니다.", tags = {"Event popup Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{eventPopupId}")
	public ResponseEntity<Void> update(
		@Min(1000000000) @PathVariable Long eventPopupId,
		@Valid @ModelAttribute EventPopupUpdateRequestDto eventPopupUpdateRequestDto
	) {
		eventPopupAdminService.update(eventPopupId, eventPopupUpdateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Event popup 조회", description = "Admin 이 Event popup 을 조회합니다.", tags = {"Event popup Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{eventPopupId}")
	public ResponseEntity<EventPopupAdminResponseDto> get(@Min(1000000000) @PathVariable Long eventPopupId) {
		return ResponseEntity.ok(eventPopupAdminService.get(eventPopupId));
	}

	@Operation(summary = "Event popup 변경 이력 조회", description = "Admin 이 Event popup 의 변경 이력을 조회", tags = {
		"Event popup Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{eventPopupId}/revisions")
	public ResponseEntity<PageContentResponseDto<EventPopupRevisionResponseDto>> getEventPopupRevisions(
		@Min(1000000000) @PathVariable Long eventPopupId,
		@ModelAttribute PageRequest pageRequest
	) {
		return ResponseEntity.ok(eventPopupAdminService.getEventPopupRevisions(eventPopupId, pageRequest.of()));
	}
}
