package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.hubspot.ExternalServiceMappingSearchDto;
import com.virnect.account.adapter.inbound.dto.request.hubspot.ExternalServiceMappingSyncableRequestDto;
import com.virnect.account.adapter.inbound.dto.response.ExternalServiceMappingRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.HubspotMappingResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.ExternalServiceMappingService;

@Tag(name = "HubSpot Admin", description = "허브 스팟 Admin API")
@RestController
@RequestMapping("/api/admin/hubspot")
@RequiredArgsConstructor
public class HubspotAdminController {
	private final ExternalServiceMappingService externalServiceMappingService;

	@Operation(summary = "HubSpot Data Mapping 결과 목록 조회", description = "HubSpot Data Mapping 결과(true/false)를 조회합니다.",
		tags = {
			"HubSpot Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/external-service-mappings")
	public ResponseEntity<PageContentResponseDto<HubspotMappingResponseDto>> getHubspotServiceMappings(
		@ModelAttribute ExternalServiceMappingSearchDto externalServiceMappingSearchDto,
		PageRequest pageRequest
	) {
		PageContentResponseDto<HubspotMappingResponseDto> responseMessage = externalServiceMappingService.getHubspotServiceMappings(
			externalServiceMappingSearchDto,
			pageRequest.of()
		);
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "HubSpot And User Data Mapping 정보 Synchronization", description = "HubSpot And User Data Mapping 정보 Synchronization",
		tags = {
			"HubSpot Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping("/external-service-mappings/users/{userId}/synchronize")
	public ResponseEntity<Void> synchronizeUserAndHubspotMapping(
		@Min(1000000000) @PathVariable("userId") Long userId
	) {
		externalServiceMappingService.synchronizeUserAndHubspotMapping(userId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "HubSpot And Organization Data Mapping 정보 Synchronization", description = "HubSpot And Organization Data Mapping 정보 Synchronization",
		tags = {
			"HubSpot Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping("/external-service-mappings/organizations/{organizationId}/synchronize")
	public ResponseEntity<Void> synchronizeOrganizationAndHubspotMapping(
		@Min(1000000000) @PathVariable("organizationId") Long organizationId
	) {
		externalServiceMappingService.synchronizeOrganizationAndHubspotMapping(organizationId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "HubSpot And Item Data Mapping 정보 Synchronization", description = "HubSpot And Item Data Mapping 정보 Synchronization",
		tags = {
			"HubSpot Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping("/external-service-mappings/items/{itemId}/synchronize")
	public ResponseEntity<Void> synchronizeItemAndHubspotMapping(
		@Min(1000000000) @PathVariable("itemId") Long itemId
	) {
		externalServiceMappingService.synchronizeItemAndHubspotMapping(itemId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "External Service Mapping 변경 이력 조회", description = "Admin External Service Mapping 변경 이력 조회", tags = "HubSpot Admin")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/external-service-mappings/{externalServiceMappingId}/revisions")
	public ResponseEntity<PageContentResponseDto<ExternalServiceMappingRevisionResponseDto>> getRevisions(
		@Min(1000000000) @PathVariable("externalServiceMappingId") Long externalServiceMappingId,
		PageRequest pageRequest
	) {
		PageContentResponseDto<ExternalServiceMappingRevisionResponseDto> externalServiceMappingRevisions =
			externalServiceMappingService.getRevisions(externalServiceMappingId, pageRequest.of());
		return new ResponseEntity<>(externalServiceMappingRevisions, HttpStatus.OK);
	}

	@Operation(summary = "External Service Mapping syncable 변경", description = "External Service Mapping syncable 변경", tags = "HubSpot Admin")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/external-service-mappings/{externalServiceMappingId}/syncable")
	public ResponseEntity<Void> updateSyncable(
		@Min(1000000000) @PathVariable("externalServiceMappingId") Long externalServiceMappingId,
		@RequestBody @Valid ExternalServiceMappingSyncableRequestDto externalServiceMappingSyncableRequestDto
	) {
		externalServiceMappingService.updateSyncable(
			externalServiceMappingId, externalServiceMappingSyncableRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
