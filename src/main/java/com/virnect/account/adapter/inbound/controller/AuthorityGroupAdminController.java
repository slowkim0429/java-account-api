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
import com.virnect.account.adapter.inbound.dto.request.UseStatusUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupModifyRequestDto;
import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.authoritygroup.AuthorityGroupResponseDto;
import com.virnect.account.adapter.inbound.dto.response.authoritygroup.AuthorityGroupRevisionResponseDto;
import com.virnect.account.port.inbound.AuthorityGroupService;

@Api
@Validated
@Tag(name = "Authority Group Admin", description = "Authority Group에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/authority-groups")
public class AuthorityGroupAdminController {
	private static final String AUTHORITY_GROUP_ADMIN_TAG = "Authority Group Admin";
	private final AuthorityGroupService authorityGroupService;

	@Operation(summary = "Authority Group 목록 조회", description = "Authority Group 목록 조회 API", tags = AUTHORITY_GROUP_ADMIN_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<AuthorityGroupResponseDto>> getAuthorityGroups(
		@ModelAttribute @Valid AuthorityGroupSearchDto authorityGroupSearchDto, PageRequest pageRequest
	) {
		PageContentResponseDto<AuthorityGroupResponseDto> authorityGroupResponseDtos = authorityGroupService.getAuthorityGroups(
			authorityGroupSearchDto, pageRequest.of());
		return new ResponseEntity<>(authorityGroupResponseDtos, HttpStatus.OK);
	}

	@Operation(summary = "Admin Authority group 조회", description = "Admin 이 Authority group 을 조회합니다.", tags = AUTHORITY_GROUP_ADMIN_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{authorityGroupId}")
	public ResponseEntity<AuthorityGroupResponseDto> getAuthorityGroupResponse(
		@Min(10000000000L) @PathVariable Long authorityGroupId
	) {
		AuthorityGroupResponseDto authorityGroupResponse = authorityGroupService.getAuthorityGroupResponse(
			authorityGroupId);
		return new ResponseEntity<>(authorityGroupResponse, HttpStatus.OK);
	}

	@Operation(summary = "Authority Group 생성", description = "Authority Group 생성 API", tags = AUTHORITY_GROUP_ADMIN_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping
	public ResponseEntity<Void> create(
		@RequestBody @Valid AuthorityGroupCreateRequestDto authorityGroupCreateRequestDto
	) {
		authorityGroupService.create(authorityGroupCreateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Authority Group 수정", description = "Authority Group 수정 API", tags = AUTHORITY_GROUP_ADMIN_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{authorityGroupId}")
	public ResponseEntity<Void> modify(
		@Min(10000000000L) @PathVariable Long authorityGroupId,
		@RequestBody @Valid AuthorityGroupModifyRequestDto authorityGroupModifyRequestDto
	) {
		authorityGroupService.modify(authorityGroupId, authorityGroupModifyRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Authority Group 사용 상태 변경", description = "Authority Group 사용 상태 변경 API", tags = AUTHORITY_GROUP_ADMIN_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{authorityGroupId}/status")
	public ResponseEntity<Void> updateStatus(
		@Min(10000000000L) @PathVariable Long authorityGroupId,
		@RequestBody @Valid UseStatusUpdateRequestDto useStatusUpdateRequestDto
	) {
		authorityGroupService.updateStatus(authorityGroupId, useStatusUpdateRequestDto.statusValueOf());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Authority Group 변경 이력 조회", description = "Authority Group 변경 이력 조회 API", tags = AUTHORITY_GROUP_ADMIN_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{authorityGroupId}/revisions")
	public ResponseEntity<PageContentResponseDto<AuthorityGroupRevisionResponseDto>> revisions(
		@PathVariable @Min(10000000000L) Long authorityGroupId,
		@ModelAttribute PageRequest pageRequest
	) {
		PageContentResponseDto<AuthorityGroupRevisionResponseDto> revisionResponseDtos = authorityGroupService.getRevisions(
			authorityGroupId, pageRequest.of());
		return new ResponseEntity<>(revisionResponseDtos, HttpStatus.OK);
	}
}
