package com.virnect.account.adapter.inbound.controller;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseSearchDto;
import com.virnect.account.adapter.inbound.dto.request.validate.ApprovalStatusSubset;
import com.virnect.account.adapter.inbound.dto.response.LicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.LicenseService;

@Api
@Validated
@Tag(name = "License Admin", description = "License에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/licenses")
public class LicenseAdminController {
	private final LicenseService licenseService;

	@Operation(summary = "License 상세 조회", description = "권한이 있는 사용자가 License ID로 조회", tags = {"License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{licenseId}")
	public ResponseEntity<LicenseResponseDto> getLicense(
		@Min(1000000000) @PathVariable Long licenseId
	) {
		LicenseResponseDto responseDto = licenseService.getLicense(licenseId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@Operation(summary = "License 목록 조회", description = "권한이 있는 사용자가 License 목록 조회", tags = {"License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<LicenseResponseDto>> getLicenses(
		@Valid @ModelAttribute LicenseSearchDto licenseSearchDto,
		PageRequest pageRequest
	) {
		PageContentResponseDto<LicenseResponseDto> responseDto = licenseService.getLicenses(
			licenseSearchDto, pageRequest.of()
		);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@Operation(summary = "License 수정", description = "admin 사용자가 License 수정", tags = {"License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER')")
	@PutMapping("/{licenseId}")
	public ResponseEntity<Void> update(
		@Min(1000000000) @PathVariable("licenseId") Long licenseId,
		@RequestBody @Valid LicenseRequestDto licenseRequestDto
	) {
		licenseService.update(licenseId, licenseRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "License 등록", description = "Admin 사용자의 License 등록", tags = {"License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping
	public ResponseEntity<Void> create(
		@RequestBody @Valid LicenseRequestDto licenseRequestDto
	) {
		licenseService.create(licenseRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "License 상태변경", description = "admin 사용자가 License 상태변경", tags = {"License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{licenseId}/status/{status}")
	public ResponseEntity<Void> updateByStatus(
		@Min(1000000000) @PathVariable("licenseId") Long licenseId,
		@ApprovalStatusSubset(anyOf = {ApprovalStatus.REGISTER, ApprovalStatus.APPROVED, ApprovalStatus.REJECT})
		@PathVariable("status") String status
	) {
		if (!ApprovalStatus.isAlterableAuthorityStatus(status)) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}
		licenseService.updateByStatus(licenseId, ApprovalStatus.valueOf(status));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "License 변경 이력 조회", description = "Admin 사용자가 License ID로 변경 이력 조회", tags = {"License Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{licenseId}/revisions")
	public ResponseEntity<PageContentResponseDto<LicenseRevisionResponseDto>> getLicenseRevisions(
		@Min(1000000000) @PathVariable("licenseId") Long licenseId,
		PageRequest pageRequest
	) {
		PageContentResponseDto<LicenseRevisionResponseDto> responseDto = licenseService.getLicenseRevisions(
			licenseId, pageRequest.of());
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
