package com.virnect.account.adapter.inbound.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.user.UserSearchDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserStatisticsSearchDto;
import com.virnect.account.adapter.inbound.dto.response.JoinedUserStatisticsResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserStatisticsResponseDto;
import com.virnect.account.port.inbound.UserService;

@Api
@Validated
@Tag(name = "User By Admin", description = "User By Admin 에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class UserAdminController {
	private final UserService userService;

	@Operation(summary = "User List", description = "Admin 사용자가 User 조회", tags = {"User Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/users")
	public ResponseEntity<PageContentResponseDto<UserResponseDto>> getUsers(
		@ModelAttribute UserSearchDto userSearchDto,
		@ApiIgnore PageRequest pageable
	) {
		PageContentResponseDto<UserResponseDto> responseMessage = userService.getUsers(
			userSearchDto, pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

	@Operation(summary = "User 조회", description = "Admin 사용자가 User ID로 사용자 조회", tags = {"User Admin"})
	@ApiImplicitParam(name = "userId", dataType = "string", value = "User ID", required = true)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/users/{userId}")
	public ResponseEntity<UserResponseDto> getUserById(
		@Min(1000000000) @PathVariable(name = "userId") Long userId
	) {
		UserResponseDto responseMessage = userService.getUserById(userId);
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "User sync", description = "Admin용 User sync", tags = {"User Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping("/users/{userId}/synchronize")
	public ResponseEntity<Void> syncUser(
		@Min(1000000000) @PathVariable Long userId
	) {
		userService.sync(userId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "User 변경 이력 조회", description = "User 변경 이력 조회", tags = {"User Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/users/{userId}/revisions")
	public ResponseEntity<List<UserRevisionResponseDto>> getUserRevision(
		@Min(1000000000) @PathVariable Long userId
	) {
		List<UserRevisionResponseDto> userRevision = userService.getUserRevision(userId);
		return new ResponseEntity<>(userRevision, HttpStatus.OK);
	}

	@Operation(summary = "user 통계 조회", tags = {"User Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER','ROLE_ADMIN_MASTER')")
	@GetMapping("/users/statistics")
	public ResponseEntity<UserStatisticsResponseDto> getStatistics(
		@ModelAttribute @Valid UserStatisticsSearchDto userStatisticsSearchDto
	) {
		UserStatisticsResponseDto userStatisticsResponseDto = userService.getStatistics(
			userStatisticsSearchDto);
		return ResponseEntity.ok(userStatisticsResponseDto);
	}

	@Operation(summary = "joined user 통계 조회", tags = {"User Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER','ROLE_ADMIN_MASTER')")
	@GetMapping("/users/statistics/joined-users")
	public ResponseEntity<JoinedUserStatisticsResponseDto> getStatisticsByJoinedUser() {
		JoinedUserStatisticsResponseDto joinedUserStatisticsResponseDto = userService.getStatisticsByJoinedUser();
		return ResponseEntity.ok(joinedUserStatisticsResponseDto);
	}
}
