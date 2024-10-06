package com.virnect.account.adapter.inbound.controller;

import org.springframework.http.HttpStatus;
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

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAndAttributeResponseDto;
import com.virnect.account.port.inbound.OrganizationLicenseService;

@Api
@Validated
@Tag(name = "Organization License", description = "Organization License에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations/licenses")
public class OrganizationLicenseController {
	private static final String ORGANIZATION_LICENSE_TAG = "Organization License";

	private final OrganizationLicenseService organizationLicenseService;

	@Operation(summary = "현재 사용자의 Organization License 조회", description = "현재 사용자의 Organization License 조회", tags = ORGANIZATION_LICENSE_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@GetMapping("/my")
	public ResponseEntity<OrganizationLicenseAndAttributeResponseDto> getMyOrganizationLicense(
	) {
		OrganizationLicenseAndAttributeResponseDto responseMessage = organizationLicenseService.getMyOrganizationLicense();
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}
}
