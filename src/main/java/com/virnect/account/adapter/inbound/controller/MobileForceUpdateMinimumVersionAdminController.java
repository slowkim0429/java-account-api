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

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.appversion.MobileForceUpdateMinimumVersionUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.MobileForceUpdateMinimumVersionService;

@Api
@Validated
@Tag(name = "Mobile force update minimum version Admin", description = "App version admin api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/mobile-managements/force-update-minimum-versions")
public class MobileForceUpdateMinimumVersionAdminController {

	private static final String MOBILE_MANAGEMENT_ADMIN_VERSION_TAG = "Mobile force update minimum version Admin";
	private final MobileForceUpdateMinimumVersionService mobileForceUpdateMinimumVersionService;

	@Operation(summary = "Admin App version 강제 업데이트 최소 버전 상세 조회",
		description = "Admin App version 강제 업데이트 최소 버전 상세 조회 api",
		tags = {MOBILE_MANAGEMENT_ADMIN_VERSION_TAG})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<MobileForceUpdateMinimumVersionAdminResponseDto> getMobileForceUpdateMinimumVersion() {
		return ResponseEntity.ok(mobileForceUpdateMinimumVersionService.getForceUpdateMinimumVersionByAdmin());
	}

	@Operation(summary = "Admin App version 강제 업데이트 수정",
		description = "Admin App version 강제 업데이트 수정 api",
		tags = MOBILE_MANAGEMENT_ADMIN_VERSION_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping
	public ResponseEntity<Void> updateMobileForceUpdateMinimumVersion(
		@Valid @RequestBody MobileForceUpdateMinimumVersionUpdateRequestDto mobileForceUpdateMinimumVersionUpdateRequestDto
	) {
		mobileForceUpdateMinimumVersionService.updateForceUpdateMinimumVersion(
			mobileForceUpdateMinimumVersionUpdateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Admin App version 강제 업데이트 기록",
		description = "Admin App version 강제 업데이트 기록 조회 api",
		tags = MOBILE_MANAGEMENT_ADMIN_VERSION_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/revisions")
	public ResponseEntity<PageContentResponseDto<MobileForceUpdateMinimumVersionRevisionResponseDto>> getMobileForceUpdateMinimumVersionRevisions(
		PageRequest pageRequest
	) {
		PageContentResponseDto<MobileForceUpdateMinimumVersionRevisionResponseDto> mobileForceUpdateMinimumVersionRevisions = mobileForceUpdateMinimumVersionService.getMobileForceUpdateMinimumVersionRevisions(
			pageRequest.of());
		return new ResponseEntity<>(mobileForceUpdateMinimumVersionRevisions, HttpStatus.OK);
	}
}
