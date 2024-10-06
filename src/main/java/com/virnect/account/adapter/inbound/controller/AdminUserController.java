package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.adminuser.AdminUserAuthorityGroupRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.AdminUserSearchDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.validate.ApprovalStatusSubset;
import com.virnect.account.adapter.inbound.dto.response.AdminUserResponseDto;
import com.virnect.account.adapter.inbound.dto.response.AdminUserRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.port.inbound.AdminUserService;

@Api
@Validated
@Tag(name = "Admin User", description = "Admin User에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminUserController {
	private final String ADMIN_USER_TAG = "Admin User";

	private final AdminUserService adminUserService;

	@Operation(summary = "Admin용 로그인된 사용자 조회", description = "Admin용 로그인된 사용자의 등록 정보", tags = ADMIN_USER_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/users/me")
	public ResponseEntity<AdminUserResponseDto> getCurrentAdminUser() {
		AdminUserResponseDto responseMessage = adminUserService.getCurrentAdminUser();
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "Admin User 목록 조회", description = "Admin 사용자가 Admin 유저 신청 목록을 조회", tags = ADMIN_USER_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/admin-users")
	public ResponseEntity<PageContentResponseDto<AdminUserResponseDto>> getAdminUsers(
		@ModelAttribute AdminUserSearchDto adminUserSearchDto,
		@ApiIgnore PageRequest pageable
	) {
		PageContentResponseDto<AdminUserResponseDto> responseMessage = adminUserService.getAdminUsers(
			adminUserSearchDto, pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "Admin User 상세 조회", description = "Admin User 상세 조회", tags = ADMIN_USER_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/admin-users/{adminUserId}")
	public ResponseEntity<AdminUserResponseDto> getAdminUserById(
		@Min(1000000000) @PathVariable(name = "adminUserId") Long adminUserId
	) {
		AdminUserResponseDto responseMessage = adminUserService.getAdminUserResponse(adminUserId);
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "Admin User 승인 상태 변경", description = "Admin 사용자가 Admin 유저 신청 상태를 변경", tags = ADMIN_USER_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/admin-users/{adminUserId}/approval-status/{approvalStatus}")
	public ResponseEntity<Void> updateByStatus(
		@Min(1000000000) @PathVariable("adminUserId") Long adminUserId,
		@NotBlank @ApprovalStatusSubset(anyOf = {ApprovalStatus.APPROVED, ApprovalStatus.REJECT})
		@PathVariable("approvalStatus") String approvalStatus
	) {
		adminUserService.updateApprovalStatus(adminUserId, ApprovalStatus.valueOf(approvalStatus));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "어드민유저 비밀번호 재설정(로그인한 유저)", description = "어드민유저 비밀번호 재설정(로그인한 유저)", tags = ADMIN_USER_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/admin-users/password")
	public ResponseEntity<Void> updatePassword(
		@RequestBody @Valid UserUpdatePasswordRequestDto updatePasswordRequestDto
	) {
		adminUserService.updatePassword(updatePasswordRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "어드민 유저 탈퇴", description = "어드민 유저 탈퇴", tags = ADMIN_USER_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping("/admin-users/resign")
	public ResponseEntity<Void> resign(
		@RequestBody @Valid PasswordVerificationRequestDto passwordVerificationRequestDto
	) {
		adminUserService.resign(passwordVerificationRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "어드민 마스터에 의한 어드민 유저 탈퇴", description = "어드민 마스터에 의한 어드민 유저 탈퇴", tags = ADMIN_USER_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_MASTER')")
	@PostMapping("/admin-users/{adminUserId}/withdrawal")
	public ResponseEntity<Void> withdrawAdminUser(
		@Min(1000000000) @PathVariable(name = "adminUserId") Long adminUserId
	) {
		adminUserService.resignByAdminMaster(adminUserId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Admin User 변경 이력 조회", description = "Admin 사용자의 Admin User 변경 이력 조회", tags = ADMIN_USER_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/admin-users/{adminUserId}/revisions")
	public ResponseEntity<PageContentResponseDto<AdminUserRevisionResponseDto>> getAdminUserRevisions(
		@Min(1000000000) @PathVariable("adminUserId") Long adminUserId, PageRequest pageRequest
	) {
		PageContentResponseDto<AdminUserRevisionResponseDto> adminUserRevisions = adminUserService.getAdminUserRevisions(
			adminUserId, pageRequest.of());
		return new ResponseEntity<>(adminUserRevisions, HttpStatus.OK);
	}

	@Operation(summary = "Admin User 의 Authority group 변경", description = "Admin 사용자의 Authority group 변경", tags = ADMIN_USER_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/admin-users/{adminUserId}/authority-group")
	public ResponseEntity<Void> updateAuthorityGroup(
		@Min(1000000000) @PathVariable("adminUserId") Long adminUserId,
		@Valid @RequestBody AdminUserAuthorityGroupRequestDto adminUserAuthorityGroupRequestDto
	) {
		adminUserService.updateAuthorityGroup(adminUserId, adminUserAuthorityGroupRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
