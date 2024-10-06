package com.virnect.account.adapter.inbound.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.HubSpotAccessTokenResponseDto;
import com.virnect.account.port.inbound.HubspotService;

@Tag(name = "HubSpot", description = "허브 스팟 OAuth 인증 API")
@RestController
@RequestMapping("/api/hubspot")
@RequiredArgsConstructor
public class HubSpotController {
	private final HubspotService hubSpotService;

	@Operation(summary = "HubSpot API Token 발급", description = "HubSpot API를 호출할 수 있는 Access Token을 발급받습니다.", tags = {
		"HubSpot"})
	@PostMapping("/token")
	public ResponseEntity<HubSpotAccessTokenResponseDto> getHubSpotToken() {
		HubSpotAccessTokenResponseDto hubSpotAccessTokenResponseDto = hubSpotService.getHubSpotToken();
		return new ResponseEntity<>(hubSpotAccessTokenResponseDto, HttpStatus.OK);
	}
}
