package com.virnect.account.adapter.inbound.controller;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseSearchDto;
import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseTrackSdkUsageSearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseDetailAndItemResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseTrackSdkUsageResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.port.inbound.OrganizationLicenseAdditionalAttributeService;
import com.virnect.account.port.inbound.OrganizationLicenseAttributeService;
import com.virnect.account.port.inbound.OrganizationLicenseService;

@Api
@Validated
@Tag(name = "Organization License Admin", description = "Organization License Admin에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class OrganizationLicenseAdminController {
	private final OrganizationLicenseService organizationLicenseService;
	private final OrganizationLicenseAttributeService organizationLicenseAttributeService;
	private final OrganizationLicenseAdditionalAttributeService organizationLicenseAdditionalAttributeService;

	@Operation(summary = "Get In Use Organization License", description = "사용중인 Organization License 정보 조회", tags = {
		"Organization License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER','ROLE_ADMIN_MASTER')")
	@GetMapping("/organizations/{organizationId}/licenses/in-use")
	public ResponseEntity<List<OrganizationLicenseDetailAndItemResponseDto>> getUsingOrganizationLicense(
		@Min(1000000000) @PathVariable Long organizationId
	) {
		List<OrganizationLicenseDetailAndItemResponseDto> responseMessage = organizationLicenseService.getUsingOrganizationLicenseAndItem(
			organizationId);
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "Organization License Data Synchronize With Associate Api Servers", description = "Organization License 정보 전파", tags = {
		"Organization License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER','ROLE_ADMIN_MASTER')")
	@PostMapping("/organizations/{organizationId}/licenses/{organizationLicenseId}/synchronize")
	public ResponseEntity<Void> syncOrganizationLicense(
		@Min(1000000000) @PathVariable Long organizationId,
		@Min(1000000000) @PathVariable Long organizationLicenseId
	) {
		organizationLicenseService.sync(organizationId, organizationLicenseId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Organization License 목록 조회", description = "Organization License 목록을 조회 합니다.", tags = {
		"Organization License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER','ROLE_ADMIN_MASTER')")
	@GetMapping("/organizations/licenses")
	public ResponseEntity<PageContentResponseDto<OrganizationLicenseResponseDto>> getOrganizationLicenses(
		@ModelAttribute OrganizationLicenseSearchDto organizationLicenseSearchDto, PageRequest pageRequest
	) {
		PageContentResponseDto<OrganizationLicenseResponseDto> responseMessage = organizationLicenseService.getOrganizationLicenses(
			organizationLicenseSearchDto, pageRequest.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "Organization License 상세 조회", description = "Organization License 상세 정보를 조회합니다.", tags = {
		"Organization License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER','ROLE_ADMIN_MASTER')")
	@GetMapping("/organizations/licenses/{organizationLicenseId}")
	public ResponseEntity<OrganizationLicenseResponseDto> getOrganizationLicense(
		@Min(1000000000) @PathVariable Long organizationLicenseId
	) {
		OrganizationLicenseResponseDto responseMessage = organizationLicenseService.getOrganizationLicense(
			organizationLicenseId);
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "Get Organization License Attributes", description = "Organization License Attribute 목록 조회", tags = {
		"Organization License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER','ROLE_ADMIN_MASTER')")
	@GetMapping("/organizations/licenses/{organizationLicenseId}/attributes")
	public ResponseEntity<List<OrganizationLicenseAttributeDetailResponseDto>> getOrganizationLicenseAttributes(
		@Min(1000000000) @PathVariable("organizationLicenseId") Long organizationLicenseId
	) {
		List<OrganizationLicenseAttributeDetailResponseDto> responseMessage = organizationLicenseAttributeService.getOrganizationLicenseAttributes(
			organizationLicenseId);
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "Get Organization License Additional Attributes", description = "Organization License Additional Attribute 목록 조회", tags = {
		"Organization License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER','ROLE_ADMIN_MASTER')")
	@GetMapping("/organizations/licenses/{organizationLicenseId}/additional-attributes")
	public ResponseEntity<List<OrganizationLicenseAdditionalAttributeResponseDto>> getOrganizationLicenseAdditionalAttributes(
		@Min(1000000000) @PathVariable("organizationLicenseId") Long organizationLicenseId
	) {
		List<OrganizationLicenseAdditionalAttributeResponseDto> responseMessage = organizationLicenseAdditionalAttributeService.getOrganizationLicenseAdditionalAttributes(
			organizationLicenseId);
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "Organization License 변경 이력 조회", description = "Admin 사용자가 Organization License 변경 이력 조회", tags = {
		"Organization License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/organizations/licenses/{organizationLicenseId}/revisions")
	public ResponseEntity<List<OrganizationLicenseRevisionResponseDto>> getOrganizationLicenseRevisions(
		@Min(1000000000) @PathVariable("organizationLicenseId") Long organizationLicenseId
	) {
		List<OrganizationLicenseRevisionResponseDto> responseDto = organizationLicenseService.getOrganizationLicenseRevisions(
			organizationLicenseId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@Operation(summary = "Organization License Additional Attribute 변경 이력 조회", description = "Admin 사용자가 Organization License Additional Attribute 변경 이력 조회", tags = {
		"Organization License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/organization-licenses/{organizationLicenseId}/additional-attributes/revisions")
	public ResponseEntity<List<OrganizationLicenseAdditionalAttributeRevisionResponseDto>> getOrganizationLicenseAdditionalAttributeRevisions(
		@RequestParam("licenseAdditionalAttributeType") LicenseAdditionalAttributeType licenseAdditionalAttributeType,
		@Min(1000000000) @PathVariable("organizationLicenseId") Long organizationLicenseId
	) {
		List<OrganizationLicenseAdditionalAttributeRevisionResponseDto> responseDto = organizationLicenseAdditionalAttributeService.getRevisions(
			organizationLicenseId,
			licenseAdditionalAttributeType
		);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@Operation(summary = "Organization License Attribute 변경 이력 조회", description = "Admin 사용자가 Organization License Attribute 변경 이력 조회", tags = {
		"Organization License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/organization-licenses/{organizationLicenseId}/attributes/revisions")
	public ResponseEntity<List<OrganizationLicenseAttributeRevisionResponseDto>> getOrganizationLicenseAttributeRevisions(
		@RequestParam("licenseAttributeType") LicenseAttributeType licenseAttributeType,
		@Min(1000000000) @PathVariable("organizationLicenseId") Long organizationLicenseId
	) {
		List<OrganizationLicenseAttributeRevisionResponseDto> responseDto = organizationLicenseAttributeService.getRevisions(
			organizationLicenseId,
			licenseAttributeType
		);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@Operation(summary = "Track Sdk License 접근 기록 조회", description = "Admin 사용자가 Track Sdk License 접근 기록 조회", tags = {
		"Organization License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/organization-licenses/track-sdk-usage-histories")
	public ResponseEntity<PageContentResponseDto<OrganizationLicenseTrackSdkUsageResponseDto>> getTrackSdkUsageHistories(
		@ModelAttribute OrganizationLicenseTrackSdkUsageSearchDto organizationLicenseTrackSdkUsageSearchDto,
		PageRequest pageRequest

	) {
		PageContentResponseDto<OrganizationLicenseTrackSdkUsageResponseDto> responseDto = organizationLicenseService.getTrackSdkUsageHistories(
			organizationLicenseTrackSdkUsageSearchDto,
			pageRequest.of()
		);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
