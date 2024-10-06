package com.virnect.account.adapter.inbound.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionResponseDto;
import com.virnect.account.port.inbound.MobileForceUpdateMinimumVersionService;

@Api
@Validated
@Tag(name = "Mobile force update minimum version", description = "App version 사용자 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mobile-managements/force-update-minimum-versions")
public class MobileForceUpdateMinimumVersionController {

	private static final String MOBILE_MANAGEMENT_VERSION_TAG = "Mobile force update minimum version";
	private final MobileForceUpdateMinimumVersionService mobileForceUpdateMinimumVersionService;

	@Operation(summary = "App version 강제 업데이트 조회 api", description = "App version 강제 업데이트 최소 버전 조회 api", tags = {
		MOBILE_MANAGEMENT_VERSION_TAG})
	@GetMapping
	public ResponseEntity<MobileForceUpdateMinimumVersionResponseDto> getForceUpdateVersion() {
		return ResponseEntity.ok(mobileForceUpdateMinimumVersionService.getForceUpdateMinimumVersion());
	}
}
