package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.locale.LocaleSearchDto;
import com.virnect.account.adapter.inbound.dto.response.LocaleResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.port.inbound.LocaleService;

@Api
@Validated
@Tag(name = "Locale Admin", description = "Locale에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/locales")
public class LocaleAdminController {
	private final LocaleService localeService;

	@Operation(summary = "Locale 목록 조회", description = "Admin 사용자가 서비스 리전 목록 조회", tags = {"Locale Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<LocaleResponseDto>> getLocales(
		@ModelAttribute @Valid LocaleSearchDto localeSearchDto,
		PageRequest pageable
	) {
		PageContentResponseDto<LocaleResponseDto> responseMessage = localeService.getLocales(
			localeSearchDto, pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}
}
