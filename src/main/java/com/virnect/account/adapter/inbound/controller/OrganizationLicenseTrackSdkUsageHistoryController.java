package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseTrackSdkUsageRequestDto;
import com.virnect.account.port.inbound.OrganizationLicenseTrackSdkUsageHistoryService;

@Api
@Validated
@Tag(name = "Organization License Track Sdk Usage History", description = "Organization License Track Sdk Usage History에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organization-licenses/track-sdk-usage-histories")
public class OrganizationLicenseTrackSdkUsageHistoryController {

	private final OrganizationLicenseTrackSdkUsageHistoryService organizationLicenseTrackSdkUsageHistoryService;

	@Operation(summary = "track sdk organization license 활동 기록", description = "track sdk organization license 활동 내용 기록",
		tags = "Organization License Track Sdk Usage History")
	@PreAuthorize("hasAnyRole('ROLE_TRACK_USER')")
	@PostMapping
	public ResponseEntity<Void> createTrackSdkUsageHistory(
		@RequestBody @Valid OrganizationLicenseTrackSdkUsageRequestDto organizationLicenseTrackSdkUsageRequestDto
	) {
		organizationLicenseTrackSdkUsageHistoryService.createTrackSdkUsageHistory(
			organizationLicenseTrackSdkUsageRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
