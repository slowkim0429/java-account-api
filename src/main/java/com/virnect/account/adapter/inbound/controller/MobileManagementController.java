package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.mobilemanagement.MobileManagementPasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.response.MobileManagementNoticeResponseDto;
import com.virnect.account.port.inbound.MobileManagementService;

@Api
@Validated
@Tag(name = "Mobile management", description = "Mobile 관리 사용자 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mobile-managements")
public class MobileManagementController {

	private static final String MOBILE_MANAGEMENT_TAG = "Mobile management";
	private final MobileManagementService mobileManagementService;

	@Operation(summary = "Mobile management 비밀번호 확인", description = "Mobile management 비밀번호 확인 api", tags = {
		MOBILE_MANAGEMENT_TAG})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PostMapping("/password/verification")
	public ResponseEntity<Void> passwordVerification(
		@RequestBody @Valid MobileManagementPasswordVerificationRequestDto mobileManagementPasswordVerificationDto
	) {
		mobileManagementService.passwordVerification(mobileManagementPasswordVerificationDto.getPassword());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Mobile management 공지 조회", description = "Mobile management 공지 조회 api", tags = {
		MOBILE_MANAGEMENT_TAG})
	@GetMapping("/notices/expose")
	public ResponseEntity<MobileManagementNoticeResponseDto> getNotice() {
		return ResponseEntity.ok(mobileManagementService.getExposedNotice());
	}
}
