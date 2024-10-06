package com.virnect.account.adapter.inbound.controller;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.invite.InviteSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.invite.InviteResponseDto;
import com.virnect.account.adapter.inbound.dto.response.invite.InviteRevisionResponseDto;
import com.virnect.account.port.inbound.InviteService;

@Tag(name = "Invite Admin", description = "Admin 의 초대 내역 관리")
@RestController
@RequestMapping("/api/admin/invites")
@RequiredArgsConstructor
public class InviteAdminController {
	private final InviteService inviteService;

	@Operation(summary = "초대 내역 목록 조회", description = "초대 내역 목록 조회", tags = {"Invite Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<InviteResponseDto>> getInvites(
		@ModelAttribute InviteSearchDto inviteSearchDto, @ApiIgnore PageRequest pageable
	) {
		PageContentResponseDto<InviteResponseDto> responseMessage = inviteService.getInvites(
			inviteSearchDto, pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "초대 정보 조회", description = "초대 정보 조회", tags = {"Invite Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{inviteId}")
	public ResponseEntity<InviteResponseDto> getInvites(
		@Min(1000000000) @PathVariable("inviteId") Long inviteId
	) {
		InviteResponseDto responseMessage = inviteService.getInvite(inviteId);
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "초대 내역 히스토리 조회", description = "초대 내역 히스토리 조회", tags = {"Invite Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{inviteId}/revisions")
	public ResponseEntity<List<InviteRevisionResponseDto>> getInviteRevisions(
		@Min(1000000000) @PathVariable("inviteId") Long inviteId
	) {
		List<InviteRevisionResponseDto> responseMessage = inviteService.getInviteRevisions(inviteId);
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

}
