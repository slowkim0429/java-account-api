package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.mobilemanagement.MobileManagementUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.MobileManagementResponseDto;
import com.virnect.account.port.inbound.MobileManagementService;

@Api
@Validated
@Tag(name = "Mobile management admin", description = "Mobile 관리 관리자 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/mobile-managements")
public class MobileManagementAdminController {

	private static final String MOBILE_MANAGEMENT_ADMIN_TAG = "Mobile management admin";
	private final MobileManagementService mobileManagementService;

	@Operation(summary = "Mobile management 조회", description = "Mobile management 조회 api"
		, tags = {MOBILE_MANAGEMENT_ADMIN_TAG})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<MobileManagementResponseDto> getMobileManagement() {
		return ResponseEntity.ok(mobileManagementService.getMobileManagementResponse());
	}

	@Operation(summary = "Mobile management 수정", description = "Mobile management 수정 api"
		, tags = {MOBILE_MANAGEMENT_ADMIN_TAG})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping
	public ResponseEntity<MobileManagementResponseDto> updateMobileManagement(
		@RequestBody @Valid MobileManagementUpdateRequestDto mobileManagementUpdateRequestDto
	) {
		mobileManagementService.update(mobileManagementUpdateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
