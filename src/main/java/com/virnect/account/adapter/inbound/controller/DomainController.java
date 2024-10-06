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
import com.virnect.account.adapter.inbound.dto.request.domain.DomainSearchDto;
import com.virnect.account.adapter.inbound.dto.response.DomainResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.DomainService;

@Api
@Validated
@Tag(name = "Domain", description = "Domain에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/domains")
public class DomainController {
	private final DomainService domainService;

	@Operation(summary = "Domain 목록 조회", description = "서비스 도메인 목록 조회", tags = {"Domain"})
	@PreAuthorize("permitAll()")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<DomainResponseDto>> getDomains(
		@ModelAttribute @Valid DomainSearchDto domainSearchDto,
		PageRequest pageable
	) {
		if (!domainSearchDto.isValid()) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, domainSearchDto.getInvalidMessage());
		}
		PageContentResponseDto<DomainResponseDto> responseMessage = domainService.getDomains(domainSearchDto, pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}
}
