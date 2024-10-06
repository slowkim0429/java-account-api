package com.virnect.account.adapter.inbound.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.response.ControlModeAccessHistoryResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.ControlModeAccessHistoryService;

@Api
@Validated
@Tag(name = "Control mode access history Admin", description = "Control mode 접근 기록 Admin")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/mobile-managements/control-mode-access-histories")
public class ControlModeAccessHistoryAdminController {

	private static final String CONTROL_MODE_ACCESS_HISTORY_TAG = "Control mode access history Admin";
	private final ControlModeAccessHistoryService controlModeAccessHistoryService;

	@Operation(summary = "Control mode access history Admin 목록 조회 api",
		description = "Control mode access history Admin 목록 조회 api",
		tags = {CONTROL_MODE_ACCESS_HISTORY_TAG})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<ControlModeAccessHistoryResponseDto>> getControlModeAccessHistories(
		PageRequest pageRequest
	) {
		PageContentResponseDto<ControlModeAccessHistoryResponseDto> controlModeAccessHistories = controlModeAccessHistoryService.getControlModeAccessHistories(
			pageRequest.of());
		return ResponseEntity.ok(controlModeAccessHistories);
	}
}
