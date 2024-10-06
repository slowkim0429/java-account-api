package com.virnect.account.adapter.inbound.controller;

import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseKeySearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseKeyResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.OrganizationLicenseKeyService;

@Api
@Validated
@Tag(name = "Organization License Key Admin", description = "Organization License Key Admin에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class OrganizationLicenseKeyAdminController {

	private final OrganizationLicenseKeyService organizationLicenseKeyService;

	@Operation(summary = "Organization License Key 목록 조회", description = "Admin 사용자가 Organization License Key 목록 조회", tags = {
		"Organization License Key Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/organization-license-keys")
	public ResponseEntity<PageContentResponseDto<OrganizationLicenseKeyResponseDto>> getOrganizationLicenseKeys(
		@ModelAttribute OrganizationLicenseKeySearchDto organizationLicenseKeySearchDto, PageRequest pageRequest
	) {
		PageContentResponseDto<OrganizationLicenseKeyResponseDto> responseMessage = organizationLicenseKeyService.getOrganizationLicenseKeys(
			organizationLicenseKeySearchDto, pageRequest.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "Organization License Key 비활성화 api", description = "Admin 사용자가 Organization License Key 비활성화 api", tags = {
		"Organization License Key Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/organization-license-keys/{organizationLicenseKeyId}/unuse")
	public ResponseEntity<Void> unuseOrganizationLicenseKey(
		@Min(1000000000) @PathVariable Long organizationLicenseKeyId
	) {
		organizationLicenseKeyService.unuseOrganizationLicenseKey(organizationLicenseKeyId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
