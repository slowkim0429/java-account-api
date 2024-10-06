package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.error.ErrorLogCreateRequestDto;
import com.virnect.account.port.inbound.ErrorLogService;

@Api
@Validated
@Tag(name = "Error Log", description = "errorLog 에 대한 정보")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ErrorLogController {

	private final ErrorLogService errorLogService;

	@Operation(summary = "Error log 등록", tags = "Error Log")
	@PostMapping("/error-logs")
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	public ResponseEntity<Void> createErrorLog(
		@RequestBody @Valid ErrorLogCreateRequestDto errorLogCreateRequestDto
	) {
		errorLogService.create(errorLogCreateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
