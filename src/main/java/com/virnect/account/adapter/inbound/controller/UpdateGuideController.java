package com.virnect.account.adapter.inbound.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.adapter.inbound.dto.response.UpdateGuideResponseDto;
import com.virnect.account.domain.enumclass.ServiceType;
import com.virnect.account.port.inbound.UpdateGuideService;

@Api
@Validated
@Tag(name = "Update guide", description = "Update guide 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/update-guides")
public class UpdateGuideController {

	private final UpdateGuideService updateGuideService;

	@Operation(summary = "Update guide 최신 글 조회", description = "사용자가 Update guide 의 최신 글을 조회함", tags = {"Update guide"})
	@PreAuthorize("permitAll()")
	@GetMapping("/latest")
	public ResponseEntity<UpdateGuideResponseDto> getLatestUpdateGuide(
		@RequestParam(required = false) @CommonEnum(enumClass = ServiceType.class) String serviceType
	) {
		return ResponseEntity.ok(updateGuideService.getLatestUpdateGuide(serviceType));
	}
}
