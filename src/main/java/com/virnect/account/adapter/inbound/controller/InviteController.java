package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.account.adapter.inbound.dto.request.InviteUserAssignRequestDto;
import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.invite.InviteRequestDto;
import com.virnect.account.adapter.inbound.dto.request.invite.WorkspaceInviteRequestDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.invite.InviteResponseDto;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.InviteService;

@Tag(name = "Invite", description = "초대 내역 관리")
@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
public class InviteController {
	private final InviteService inviteService;

	@Operation(summary = "invite 내역 생성 (타 서버 호출용)", description = "invite 내역 생성", tags = {"Invite"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PostMapping
	public ResponseEntity<Void> createInvite(
		@RequestBody @Valid InviteRequestDto inviteRequestDto
	) {
		if (!inviteRequestDto.isValid()) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, inviteRequestDto.getInvalidMessage());
		}

		inviteService.createInvite(inviteRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "invite 내역 생성 (workspace-api 호출용)", description = "invite 내역 생성", tags = {"Invite"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PostMapping("/workspaces")
	public ResponseEntity<Void> createWorkspaceInvite(
		@RequestBody @Valid WorkspaceInviteRequestDto workspaceInviteRequestDto
	) {
		inviteService.createWorkspaceInvite(workspaceInviteRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "초대 승인", description = "초대한 user를 초대 내용에 맞게 등록", tags = {"Invite"})
	@PreAuthorize("permitAll()")
	@PutMapping("/assign")
	public ResponseEntity<Void> assignInviteUser(
		@RequestBody @Valid InviteUserAssignRequestDto inviteUserAssignRequestDto
	) {
		inviteService.assignUser(inviteUserAssignRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "워크스페이스 초대 내역 목록 조회", description = "워크스페이스 초대 내역 목록 조회", tags = {"Invite"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@GetMapping("/workspaces/{workspaceId}")
	public ResponseEntity<PageContentResponseDto<InviteResponseDto>> getInviteOfWorkspaceType(
		@Min(1000000000) @PathVariable("workspaceId") Long workspaceId,
		@ApiIgnore PageRequest pageable
	) {
		PageContentResponseDto<InviteResponseDto> responseMessage = inviteService.getInviteOfWorkspaceType(
			workspaceId, pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "초대 철회 (WORKSPACE)", description = "워크스페이스 초대 철회", tags = {"Invite"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PutMapping("/{inviteId}/cancel")
	public ResponseEntity<Void> cancelInvite(
		@Min(1000000000) @PathVariable("inviteId") Long inviteId
	) {
		inviteService.cancelInvite(inviteId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
