package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationContractRequestDto;
import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserResponseDto;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.OrganizationContractService;
import com.virnect.account.port.inbound.OrganizationService;
import com.virnect.account.security.service.CustomUserDetails;

@Api
@Validated
@Tag(name = "Organization", description = "Organization에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations")
public class OrganizationController {
	private final OrganizationService organizationService;
	private final OrganizationContractService organizationContractService;

	@Operation(summary = "Organization 수정", description = "Organization 수정", tags = {"Organization"})
	@PreAuthorize("hasRole('ROLE_ORGANIZATION_OWNER')")
	@PutMapping("/{organizationId}")
	public ResponseEntity<Void> update(
		@Min(1000000000) @PathVariable Long organizationId,
		@RequestBody @Valid OrganizationUpdateRequestDto organizationUpdateRequestDto
	) {
		if (!organizationUpdateRequestDto.isValid()) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, organizationUpdateRequestDto.getInvalidMessage());
		}

		organizationService.update(organizationId, organizationUpdateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(
		summary = "Organization 의 User(Owner) 조회", description = "Organization 의 User(Owner) 조회", tags = {
		"Organization"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@GetMapping("/{organizationId}/users")
	public ResponseEntity<UserResponseDto> getOrganizationUser(
		@Min(1000000000) @PathVariable("organizationId") Long organizationId
	) {
		UserResponseDto responseMessage = organizationService.getOrganizationUser(organizationId);
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "로그인된 사용자 Organization 조회", description = "로그인된 사용자의 Organization 정보", tags = {"Organization"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@GetMapping("/me")
	public ResponseEntity<OrganizationResponseDto> getMyOrganization(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		OrganizationResponseDto responseMessage = organizationService.getCurrentOrganization(customUserDetails);
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "Organization Contract 생성", description = "Organization Contract 정보를 생성합니다. stripe 정기결제 요청 시에는 Admin Master 권한으로 사용됩니다.", tags = {
		"Organization"})
	@PreAuthorize("isAuthenticated() or hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PostMapping("/contracts")
	public ResponseEntity<Void> syncOrganizationContract(
		@RequestBody @Valid OrganizationContractRequestDto organizationContractRequestDto
	) {
		if (!organizationContractRequestDto.isValid()) {
			throw new CustomException(
				ErrorCode.INVALID_INPUT_VALUE, organizationContractRequestDto.getInvalidMessage());
		}
		organizationContractService.sync(organizationContractRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
