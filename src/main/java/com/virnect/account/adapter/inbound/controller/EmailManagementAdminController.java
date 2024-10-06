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
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementRequestDto;
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementSearchDto;
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementUseStatusUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.EmailManagementResponseDto;
import com.virnect.account.adapter.inbound.dto.response.EmailManagementRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.EmailManagementService;

@Api
@Validated
@Tag(name = "Email management admin", description = "Email 템플릿 관리자 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/email-managements")
public class EmailManagementAdminController {
	private static final String EMAIL_MANAGEMENT_TAG = "Email management admin";
	private final EmailManagementService emailManagementService;

	@Operation(summary = "Email management 조회", description = "Email management 조회 api"
		, tags = EMAIL_MANAGEMENT_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<EmailManagementResponseDto>> getEmailManagements(
		@ModelAttribute EmailManagementSearchDto emailManagementSearchDto,
		PageRequest pageable
	) {
		PageContentResponseDto<EmailManagementResponseDto> responseMessage = emailManagementService.getEmailManagements(
			emailManagementSearchDto, pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "Email management 등록", description = "Email management 등록 api"
		, tags = EMAIL_MANAGEMENT_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping
	public ResponseEntity<Void> createEmailManagement(
		@ModelAttribute @Valid EmailManagementRequestDto emailManagementRequestDto
	) {
		emailManagementService.createEmailManagement(emailManagementRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Email management useStatus 변경", description = "Email management useStatus 변경 api"
		, tags = EMAIL_MANAGEMENT_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{emailManagementId}/use-status")
	public ResponseEntity<Void> updateUseStatusOfEmailManagement(
		@Min(1000000000) @PathVariable Long emailManagementId,
		@RequestBody @Valid EmailManagementUseStatusUpdateRequestDto emailManagementUseStatusUpdateRequestDto
	) {
		emailManagementService.updateUseStatusOfEmailManagement(
			emailManagementId,
			emailManagementUseStatusUpdateRequestDto
		);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Email management revision 목록 조회", description = "Email management revision 목록 조회"
		, tags = EMAIL_MANAGEMENT_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{emailManagementId}/revisions")
	public ResponseEntity<PageContentResponseDto<EmailManagementRevisionResponseDto>> getRevisions(
		@Min(1000000000) @PathVariable Long emailManagementId,
		PageRequest pageRequest
	) {
		PageContentResponseDto<EmailManagementRevisionResponseDto> emailManagementRevisions = emailManagementService.getEmailManagementRevisions(
			emailManagementId, pageRequest.of());
		return ResponseEntity.ok(emailManagementRevisions);
	}

	@Operation(summary = "Email management 정보 변경", description = "Email management 정보 변경 api"
		, tags = EMAIL_MANAGEMENT_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{emailManagementId}")
	public ResponseEntity<Void> updateEmailManagement(
		@Min(1000000000) @PathVariable Long emailManagementId,
		@ModelAttribute @Valid EmailManagementUpdateRequestDto emailManagementUpdateRequestDto
	) {
		emailManagementService.updateEmailManagement(
			emailManagementId,
			emailManagementUpdateRequestDto
		);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Email management 상세 조회", description = "Email management 상세 조회 api"
		, tags = EMAIL_MANAGEMENT_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{emailManagementId}")
	public ResponseEntity<EmailManagementResponseDto> getEmailManagement(
		@Min(1000000000) @PathVariable Long emailManagementId
	) {
		EmailManagementResponseDto responseMessage = emailManagementService.getEmailManagement(
			emailManagementId);
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}
}
