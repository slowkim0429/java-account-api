package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.AuthCodeVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.TokenRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.password.EmailChangePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.AdminUserRequestDto;
import com.virnect.account.adapter.inbound.dto.request.validate.AdminUserLoginRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.log.NoLogging;
import com.virnect.account.port.inbound.AdminUserService;
import com.virnect.account.port.inbound.AuthAdminService;
import com.virnect.account.port.inbound.EmailAuthAdminService;

@Tag(name = "Auth Admin", description = "어드민 회원 가입 및 로그인")
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AuthAdminController {
	private final AuthAdminService authAdminService;
	private final EmailAuthAdminService emailAuthAdminService;
	private final AdminUserService adminUserService;

	@Operation(summary = "어드민 회원 가입", description = "어드민 회원가입", tags = {"Auth Admin"})
	@PreAuthorize("permitAll()")
	@PostMapping("/signup")
	@NoLogging
	public ResponseEntity<Void> signUp(
		@Valid @RequestBody AdminUserRequestDto adminUserRequestDto
	) {
		authAdminService.signUp(adminUserRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "어드민 로그인", description = "어드민 로그인", tags = {"Auth Admin"})
	@PreAuthorize("permitAll()")
	@PostMapping("/login")
	@NoLogging
	public ResponseEntity<TokenResponseDto> login(
		@Valid @RequestBody AdminUserLoginRequestDto adminUserLoginRequestDto
	) {
		return ResponseEntity.ok(authAdminService.signIn(adminUserLoginRequestDto));
	}

	@Operation(summary = "어드민 로그아웃", description = "어드민 로그아웃", tags = {"Auth Admin"})
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		authAdminService.signOut();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "어드민 인증 토큰 재발급", description = "refresh token으로 token재발급", tags = {"Auth Admin"})
	@PreAuthorize("permitAll()")
	@PostMapping("/reissue")
	@NoLogging
	public ResponseEntity<TokenResponseDto> reissue(@Valid @RequestBody TokenRequestDto tokenRequestDto) {
		return ResponseEntity.ok(authAdminService.reissue(tokenRequestDto));
	}

	@Operation(summary = "비밀번호 재설정 이메일 인증 메일 전송", description = "비밀번호 재설정 이메일 인증을 위해 메일 발송", tags = {"Auth Admin"})
	@PreAuthorize("permitAll()")
	@PostMapping("/password/email")
	public ResponseEntity<Void> sendResetPasswordEmailAuthCode(
		@RequestBody @Valid EmailChangePasswordRequestDto emailChangePasswordRequestDto
	) {
		emailAuthAdminService.sendResetPasswordEmailAuthCode(emailChangePasswordRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "어드민 비밀번호 재설정 이메일 인증 코드 확인", description = "어드민 비밀번호 재설정 이메일 인증 코드 확인", tags = {"Auth Admin"})
	@PreAuthorize("permitAll()")
	@GetMapping("/password/email/verification")
	public ResponseEntity<Void> verifyResetPasswordEmailAuthCode(
		@ModelAttribute @Valid AuthCodeVerificationRequestDto authCodeVerificationRequestDto
	) {
		emailAuthAdminService.verifyResetPasswordEmailAuthCode(authCodeVerificationRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "어드민 비밀번호 재설정", description = "비밀번호 재설정", tags = {"Auth Admin"})
	@PreAuthorize("permitAll()")
	@PutMapping("/password")
	public ResponseEntity<Void> updatePasswordWithoutAuthentication(
		@RequestBody @Valid UpdatePasswordRequestDto updatePasswordRequestDto
	) {
		adminUserService.updatePasswordWithoutAuthentication(updatePasswordRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
