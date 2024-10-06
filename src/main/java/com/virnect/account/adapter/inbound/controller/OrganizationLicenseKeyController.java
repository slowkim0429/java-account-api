package com.virnect.account.adapter.inbound.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.port.inbound.OrganizationLicenseKeyService;

@Api
@Tag(name = "Organization License Key", description = "Organization License Key API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organization-license-keys")
public class OrganizationLicenseKeyController {
	private static final String ORGANIZATION_LICENSE_KEY_TAG = "Organization License Key";
	private final OrganizationLicenseKeyService organizationLicenseKeyService;

	@Operation(summary = "apply for the TRACK SDK free license", tags = ORGANIZATION_LICENSE_KEY_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PostMapping
	public ResponseEntity<Void> applyOrganizationLicenseKey() {
		organizationLicenseKeyService.applyOrganizationLicenseKey();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
