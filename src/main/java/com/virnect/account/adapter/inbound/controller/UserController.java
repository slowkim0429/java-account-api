package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdateImageRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.UserResponseDto;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.UserService;
import com.virnect.account.security.service.CustomUserDetails;

@Api
@Validated
@Tag(name = "User", description = "User에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;

	@Operation(summary = "회원 수정", description = "회원 수정", tags = {"User"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PutMapping()
	public ResponseEntity<Void> update(
		@RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto
	) {
		if (userUpdateRequestDto.isInvalid()) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, userUpdateRequestDto.getInvalidMessage());
		}
		userService.update(userUpdateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "회원 이미지 업로드", description = "회원 이미지 업로드", tags = {"User"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PostMapping(value = "/profile-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<Void> updateCurrentUserProfileImage(
		@ModelAttribute @Valid UserUpdateImageRequestDto userUpdateImageRequestDto
	) {
		userService.updateCurrentUserProfileImage(userUpdateImageRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "회원 이미지 삭제", description = "회원 이미지 삭제", tags = {"User"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PutMapping(value = "/profile-image/delete")
	public ResponseEntity<Void> deleteCurrentUserProfileImage() {
		userService.deleteCurrentUserProfileImage();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "로그인된 사용자 조회", description = "로그인된 사용자의 등록 정보", tags = {"User"})
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@GetMapping("/me")
	public ResponseEntity<UserResponseDto> getMy(
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		UserResponseDto responseMessage = userService.getUserByCurrent(customUserDetails);
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "User Email 존재여부 조회", description = "권한이 있는 사용자가 User Email로 사용자 존재여부 조회", tags = {"User"})
	@ApiImplicitParam(name = "email", dataType = "string", value = "User Email", required = true)
	@PreAuthorize("permitAll()")
	@GetMapping("/exist")
	public ResponseEntity<Boolean> getExistUserByEmail(@RequestParam("email") @NotBlank @Email String email) {
		boolean existsUser = userService.existsUserByEmail(email);
		return new ResponseEntity<>(existsUser, HttpStatus.OK);
	}

	@Operation(summary = "User 조회", description = "권한이 있는 사용자가 User Email로 사용자 조회", tags = {"User"})
	@ApiImplicitParam(name = "email", dataType = "string", value = "User Email", required = true)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER')")
	@GetMapping("/email/{email:.+}")
	public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable("email") @NotBlank @Email String email) {
		UserResponseDto responseMessage = userService.getUserResponseDtoByEmail(email);
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "비밀번호 확인", description = "비밀번호 확인", tags = {"Auth"})
	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@PostMapping("/password/verification")
	public ResponseEntity<Void> passwordVerification(
		@RequestBody @Valid PasswordVerificationRequestDto passwordVerificationRequestDto
	) {
		userService.passwordVerification(passwordVerificationRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "비밀번호 재설정(로그인한 유저)", description = "비밀번호 재설정(로그인한 유저)", tags = {"Auth"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@PutMapping("/password")
	public ResponseEntity<Void> updateCurrentUserPassword(
		@RequestBody @Valid UserUpdatePasswordRequestDto updatePasswordRequestDto
	) {
		userService.updateCurrentUserPassword(updatePasswordRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
