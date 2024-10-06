package com.virnect.account.adapter.inbound.controller;

import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.OrganizationContractResponseDto;
import com.virnect.account.port.inbound.OrganizationContractService;

@Api
@Validated
@Tag(name = "Organization Contract", description = "Organization Contract api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organization-contracts")
public class OrganizationContractController {
	private final String ORGANIZATION_CONTRACT_TAG = "Organization Contract";

	private final OrganizationContractService organizationContractService;

	@Operation(summary = "Organization Contract 상세 조회", tags = ORGANIZATION_CONTRACT_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@GetMapping("/{contractId}")
	public ResponseEntity<OrganizationContractResponseDto> getOrganizationContract(
		@Min(1000000000) @PathVariable("contractId") Long contractId
	) {
		OrganizationContractResponseDto organizationContractResponseDto = organizationContractService.getOrganizationContract(
			contractId);
		return new ResponseEntity<>(organizationContractResponseDto, HttpStatus.OK);
	}
}
