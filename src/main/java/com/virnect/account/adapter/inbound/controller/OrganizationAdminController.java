package com.virnect.account.adapter.inbound.controller;

import java.util.List;

import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationSearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationContractResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.OrganizationContractService;
import com.virnect.account.port.inbound.OrganizationService;

@Api
@Validated
@Tag(name = "Organization Admin", description = "Organization Admin에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/organizations")
public class OrganizationAdminController {
	private final OrganizationService organizationService;
	private final OrganizationContractService organizationContractService;

	@Operation(summary = "Organization 조회", description = "ADMIN이 Organization Id로 조회", tags = {"Organization Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{organizationId}")
	public ResponseEntity<OrganizationResponseDto> getOrganizationById(
		@Min(1000000000) @PathVariable("organizationId") Long organizationId
	) {
		OrganizationResponseDto responseMessage = organizationService.getOrganizationById(organizationId);
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "Organization List", description = "ADMIN이 Organization List로 조회", tags = {
		"Organization Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<OrganizationResponseDto>> getOrganizations(
		@Valid @ModelAttribute OrganizationSearchDto organizationSearchDto,
		@ApiIgnore PageRequest pageable
	) {
		PageContentResponseDto<OrganizationResponseDto> responseMessage = organizationService.getOrganizations(
			organizationSearchDto, pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "Get Organization Contract", description = "Organization Contract 정보를 조회", tags = {
		"Organization Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/contracts/{contractId}")
	public ResponseEntity<OrganizationContractResponseDto> getOrganizationContract(
		@Min(1000000000) @PathVariable("contractId") Long contractId
	) {
		OrganizationContractResponseDto responseMessage = organizationContractService.getOrganizationContractByAdmin(
			contractId);
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "Organization Data Synchronize", description = "organization 정보 전파", tags = {
		"Organization Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER','ROLE_ADMIN_MASTER')")
	@PostMapping("/{organizationId}/synchronize")
	public ResponseEntity<Void> synchronizeOrganization(
		@Min(1000000000) @PathVariable Long organizationId
	) {
		organizationService.synchronizeOrganizationByAdmin(organizationId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Organization 변경 이력 조회", description = "Organization 변경 이력 조회", tags = {"Organization Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER','ROLE_ADMIN_MASTER')")
	@GetMapping("/{organizationId}/revisions")
	public ResponseEntity<List<OrganizationRevisionResponseDto>> getOrganizationRevision(
		@Min(1000000000) @PathVariable Long organizationId
	) {
		List<OrganizationRevisionResponseDto> responseDto = organizationService.getOrganizationRevision(organizationId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

}
