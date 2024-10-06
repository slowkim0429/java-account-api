package com.virnect.account.adapter.inbound.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ServiceTimeZoneResponseDto;
import com.virnect.account.port.inbound.ServiceTimeZoneService;

@Api
@Validated
@Tag(name = "Service time zone", description = "Service time zone API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service-time-zones")
public class ServiceTimeZoneController {
	private static final String SERVICE_TIME_ZONE_TAG = "Service time zone";

	private final ServiceTimeZoneService serviceTimeZoneService;

	@Operation(summary = "Service time zone 목록 조회", description = "Service time zone 목록 조회 API", tags = SERVICE_TIME_ZONE_TAG)
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<ServiceTimeZoneResponseDto>> getServiceTimeZones(
		PageRequest pageable
	) {
		PageContentResponseDto<ServiceTimeZoneResponseDto> responseMessage = serviceTimeZoneService.getServiceTimeZones(
			pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}
}
