package com.virnect.account.adapter.inbound.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseGradeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseGradeSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.grade.LicenseGradeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.grade.LicenseGradeRevisionResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.port.inbound.LicenseGradeService;

@Api
@Validated
@Tag(name = "License Grade Admin", description = "License Grade에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/licenses/grades")
public class LicenseGradeAdminController {
	private final LicenseGradeService licenseGradeService;

	@Operation(summary = "License Grade 조회", description = "Admin 사용자가 License Grade Id로 조회", tags = {
		"License Grade Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{gradeId}")
	public ResponseEntity<LicenseGradeResponseDto> getLicenseGradeById(
		@Min(1000000000) @PathVariable("gradeId") Long gradeId
	) {
		LicenseGradeResponseDto licenseGradeResponseDto = licenseGradeService.getLicenseGradeById(gradeId);
		return new ResponseEntity<>(licenseGradeResponseDto, HttpStatus.OK);
	}

	@Operation(summary = "License Grade 등록", description = "Admin 사용자가 License Grade 등록", tags = {
		"License Grade Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping
	public ResponseEntity<Void> create(
		@RequestBody @Valid LicenseGradeRequestDto licenseGradeRequestDto
	) {
		licenseGradeService.create(licenseGradeRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "License Grade 변경", description = "Admin 사용자가 License Grade 변경", tags = {
		"License Grade Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{gradeId}")
	public ResponseEntity<Void> updateGrade(
		@Min(1000000000) @PathVariable("gradeId") Long gradeId,
		@RequestBody @Valid LicenseGradeRequestDto requestDto
	) {
		licenseGradeService.updateGrade(gradeId, requestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "License Grade Status 변경", description = "Admin 사용자가 License Grade Status 변경", tags = {
		"License Grade Admin"})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "gradeId", value = "License Grade id", required = true, dataType = "Long", paramType = "path"),
		@ApiImplicitParam(name = "status", value = "License Grade status", required = true, dataType = "ApprovalStatus", paramType = "path")
	})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{gradeId}/status/{status}")
	public ResponseEntity<Void> updateStatus(
		@Min(1000000000) @PathVariable("gradeId") Long gradeId,
		@PathVariable("status") @NotNull ApprovalStatus status
	) {
		licenseGradeService.updateStatus(gradeId, status);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "License Grade List 조회", description = "Admin 사용자가 License Grade List 조회", tags = {
		"License Grade Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<LicenseGradeResponseDto>> getLicenseGrades(
		@ModelAttribute LicenseGradeSearchDto licenseGradeSearchDto,
		PageRequest pageable
	) {
		PageContentResponseDto<LicenseGradeResponseDto> responseMessage = licenseGradeService.getLicenseGrades(
			licenseGradeSearchDto, pageable.of());
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "License Grade 변경 이력 조회", description = "Admin 사용자가 License Grade id로 변경 이력 조회", tags = {
		"License Grade Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{licenseGradeId}/revisions")
	public ResponseEntity<List<LicenseGradeRevisionResponseDto>> getLicenseGradeRevisions(
		@Min(1000000000) @PathVariable("licenseGradeId") Long licenseGradeId, PageRequest pageRequest
	) {
		List<LicenseGradeRevisionResponseDto> responseDto = licenseGradeService.getLicenseGradeRevisions(
			licenseGradeId, pageRequest.of());
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
