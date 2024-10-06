package com.virnect.account.adapter.inbound.controller;

import java.time.ZonedDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.log.NoLogging;

@Tag(name = "healthCheck", description = "healthCheck")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HealthCheckController {

	@Operation(summary = "ping", tags = {"healthCheck"})
	@GetMapping("/ping")
	@NoLogging
	public ResponseEntity<String> healthCheck() {
		String message =
			"\n\n"
				+ "------------------------------------------------------------------------------\n" + "\n"
				+ "   VIRNECT USER ACCOUNT AND AUTHENTICATION API SERVER\n"
				+ "   ---------------------------\n" + "\n"
				+ "   * SERVER_MODE: [ " + System.getenv("VIRNECT_ENV") + " ]\n" + "\n"
				+ "   * HEALTH_CHECK_DATE: [ " + ZonedDateTime.now() + " ]\n" + "\n"
				+ "------------------------------------------------------------------------------\n";
		return ResponseEntity.ok(message);
	}
}
