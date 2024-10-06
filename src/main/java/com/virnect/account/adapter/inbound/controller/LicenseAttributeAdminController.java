package com.virnect.account.adapter.inbound.controller;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.response.LicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.port.inbound.LicenseAttributeService;

@Api
@Validated
@Tag(name = "License attribute", description = "License attribute에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class LicenseAttributeAdminController {

	private final LicenseAttributeService licenseAttributeService;

	@Operation(summary = "License attribute list", description = "Admin 사용자가 License attribute 조회", tags = {
		"License attribute"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/licenses/{licenseId}/attributes/attribute")
	public ResponseEntity<List<LicenseAttributeResponseDto>> getLicenseAttributes(
		@PathVariable @Min(1000000000) Long licenseId
	) {
		List<LicenseAttributeResponseDto> licenseAttributes = licenseAttributeService.getLicenseAttributeResponse(
			licenseId);
		return ResponseEntity.ok(licenseAttributes);
	}

	@Operation(summary = "License additional attribute list", description = "Admin 사용자가 License attribute 조회", tags = {
		"License attribute"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/licenses/{licenseId}/attributes/additional")
	public ResponseEntity<List<LicenseAdditionalAttributeResponseDto>> getLicenseAdditionalAttributes(
		@PathVariable @Min(1000000000) Long licenseId
	) {
		List<LicenseAdditionalAttributeResponseDto> licenseAttributes = licenseAttributeService.getLicenseAdditionalAttributeResponse(
			licenseId);
		return ResponseEntity.ok(licenseAttributes);
	}

	@Operation(summary = "License Attribute 변경 이력 조회", description = "Admin 사용자가 License Attribute 변경 이력 조회", tags = {
		"License attribute"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/licenses/{licenseId}/attributes/revisions")
	public ResponseEntity<PageContentResponseDto<LicenseAttributeRevisionResponseDto>> getLicenseAttributeRevisions(
		@Min(1000000000) @PathVariable("licenseId") Long licenseId,
		@RequestParam("licenseAttributeType") LicenseAttributeType licenseAttributeType,
		PageRequest pageRequest
	) {
		PageContentResponseDto<LicenseAttributeRevisionResponseDto> responseDto = licenseAttributeService.getLicenseAttributeRevisions(
			licenseId, licenseAttributeType, pageRequest.of());
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@Operation(summary = "License additional Attribute 변경 이력 조회", description = "Admin 사용자가 License additional Attribute 변경 이력 조회", tags = {
		"License attribute"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/licenses/{licenseId}/attributes/additional/revisions")
	public ResponseEntity<PageContentResponseDto<LicenseAttributeRevisionResponseDto>> getLicenseAdditionalAttributeRevisions(
		@Min(1000000000) @PathVariable("licenseId") Long licenseId,
		@RequestParam("licenseAdditionalAttributeType") LicenseAdditionalAttributeType licenseAdditionalAttributeType,
		PageRequest pageRequest
	) {
		PageContentResponseDto<LicenseAttributeRevisionResponseDto> responseDto = licenseAttributeService.getLicenseAdditionalAttributeRevisions(
			licenseId, licenseAdditionalAttributeType, pageRequest.of());
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
