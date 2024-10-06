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
import com.virnect.account.adapter.inbound.dto.request.EmailAuthRequestDto;
import com.virnect.account.adapter.inbound.dto.request.LoginRequestDto;
import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.TokenRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.password.EmailChangePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.log.NoLogging;
import com.virnect.account.port.inbound.AuthService;
import com.virnect.account.port.inbound.EmailAuthService;
import com.virnect.account.port.inbound.UserService;

@Tag(name = "Auth", description = "회원 가입 및 로그인")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final EmailAuthService emailAuthService;
	private final UserService userService;

	@Operation(summary = "회원 가입 이메일 인증 메일 전송", description = "회원 등록 시 이메일 인증을 위해 메일 발송", tags = {"Auth"})
	@PreAuthorize("permitAll()")
	@PostMapping("/email")
	public ResponseEntity<Void> emailAuth(
		@RequestBody @Valid EmailAuthRequestDto emailAuthRequestDto
	) {
		emailAuthService.emailAuthentication(emailAuthRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "비밀번호 재설정", description = "비밀번호 재설정", tags = {"Auth"})
	@PreAuthorize("permitAll()")
	@PutMapping("/password")
	public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordRequestDto updatePasswordRequestDto) {
		userService.updatePassword(updatePasswordRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "비밀번호 변경 이메일 인증 메일 전송", description = "패스워드 변경 시 이메일 인증을 위해 메일 발송", tags = {"Auth"})
	@PreAuthorize("permitAll()")
	@PostMapping("/password/email")
	public ResponseEntity<Void> sendChangePasswordAuthEmail(
		@RequestBody @Valid EmailChangePasswordRequestDto emailChangePasswordRequestDto
	) {
		emailAuthService.sendChangePasswordAuthEmail(emailChangePasswordRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "이메일 인증 코드 확인", description = "회원가입 이메일 인증 코드 확인", tags = {"Auth"})
	@PreAuthorize("permitAll()")
	@GetMapping("/email/verification")
	public ResponseEntity<Void> emailAuthCodeVerification(
		@ModelAttribute @Valid AuthCodeVerificationRequestDto authCodeVerificationRequestDto
	) {
		emailAuthService.emailAuthCodeVerification(authCodeVerificationRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "회원 가입", description = "회원가입", tags = {"Auth"})
	@PreAuthorize("permitAll()")
	@PostMapping("/signup")
	@NoLogging
	public ResponseEntity<Void> signup(
		@RequestBody @Valid UserCreateRequestDto userCreateRequestDto
	) {
		authService.signup(userCreateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "로그인", description = "회원 로그인", tags = {"Auth"})
	@PreAuthorize("permitAll()")
	@PostMapping("/login")
	@NoLogging
	public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
		return ResponseEntity.ok(authService.signIn(loginRequestDto));
	}

	@Operation(summary = "로그아웃", description = "회원 로그아웃", tags = {"Auth"})
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		authService.signOut();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "인증 토큰 재발급", description = "refresh token으로 access token 재발급", tags = {"Auth"})
	@PreAuthorize("permitAll()")
	@PostMapping("/reissue")
	@NoLogging
	public ResponseEntity<TokenResponseDto> reissue(@Valid @RequestBody TokenRequestDto tokenRequestDto) {
		if (!tokenRequestDto.isValid()) {
			throw new CustomException(ErrorCode.INVALID_TOKEN, tokenRequestDto.getInvalidMessage());
		}
		return ResponseEntity.ok(authService.reissue(tokenRequestDto));
	}

	@Operation(summary = "탈퇴", description = "회원 탈퇴", tags = {"Auth"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PostMapping("/resign")
	public ResponseEntity<Void> resign(
		@RequestBody @Valid PasswordVerificationRequestDto passwordVerificationRequestDto
	) {
		authService.resign(passwordVerificationRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
