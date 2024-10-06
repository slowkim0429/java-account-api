package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.error.ErrorLogCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.error.ErrorLogSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorLogDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorLogResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.ErrorLogService;

@Api
@Validated
@Tag(name = "Admin Error Log", description = "Admin errorLog 에 대한 정보")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ErrorLogAdminController {
	private final ErrorLogService errorLogService;

	@Operation(summary = "Error log 등록", tags = "Admin Error Log")
	@PostMapping("/error-logs")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	public ResponseEntity<Void> createErrorLog(
		@RequestBody @Valid ErrorLogCreateRequestDto errorLogCreateRequestDto
	) {
		errorLogService.create(errorLogCreateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Error Log List", description = "Admin 개발자가 에러를 파악하기 위한 페이지", tags = "Admin Error Log")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/error-logs")
	public ResponseEntity<PageContentResponseDto<ErrorLogResponseDto>> getErrorLogs(
		@Valid @ModelAttribute ErrorLogSearchDto errorLogSearchDto,
		PageRequest pageable
	) {
		PageContentResponseDto<ErrorLogResponseDto> responseMessage =
			errorLogService.getErrorLogs(errorLogSearchDto, pageable.of());
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "Error Log Detail", description = "Admin 개발자가 에러를 확인하기 위한 상세조회", tags = "Admin Error Log")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/error-logs/{errorLogId}")
	public ResponseEntity<ErrorLogDetailResponseDto> getDetailById(
		@PathVariable Long errorLogId
	) {
		ErrorLogDetailResponseDto responseMessage = errorLogService.getErrorLog(errorLogId);
		return ResponseEntity.ok(responseMessage);
	}
}
