package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideExposeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideMediaRequestDto;
import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideRequestDto;
import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UpdateGuideMediaResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UpdateGuideResponseDto;
import com.virnect.account.port.inbound.UpdateGuideFileService;
import com.virnect.account.port.inbound.UpdateGuideService;

@Api
@Tag(name = "Update guide Admin", description = "Update guide Admin 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/update-guides")
public class UpdateGuideAdminController {

	private final UpdateGuideService updateGuideService;
	private final UpdateGuideFileService updateGuideFileService;

	@Operation(summary = "Update guide Admin 목록 조회", description = "admin 사용자가 Update guide 목록", tags = {
		"Update guide Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<UpdateGuideResponseDto>> getUpdateGuides(
		@ModelAttribute UpdateGuideSearchDto updateGuideSearchDto,
		PageRequest pageRequest
	) {
		PageContentResponseDto<UpdateGuideResponseDto> responseMessage = updateGuideService.getUpdateGuides(
			updateGuideSearchDto, pageRequest.of());
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "Update guide Admin 상세 조회", description = "admin 사용자가 Update guide 상세 조회", tags = {
		"Update guide Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{updateGuideId}")
	public ResponseEntity<UpdateGuideResponseDto> getUpdateGuideResponse(@PathVariable Long updateGuideId) {
		return ResponseEntity.ok(updateGuideService.getUpdateGuideResponse(updateGuideId));
	}

	@Operation(summary = "Update guide Admin 동영상 및 이미지 업로드", description = "파일 업로드", tags = {"Update guide Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping(value = "/media", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<UpdateGuideMediaResponseDto> mediaUpload(
		@ModelAttribute @Valid UpdateGuideMediaRequestDto updateGuideMediaRequestDto
	) {
		String fileUrl = updateGuideFileService.mediaUpload(updateGuideMediaRequestDto.getFile());
		return ResponseEntity.ok(new UpdateGuideMediaResponseDto(fileUrl));
	}

	@Operation(summary = "Update guide Admin 생성", description = "Update guide admin 생성", tags = {"Update guide Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping
	public ResponseEntity<Void> create(
		@RequestBody @Valid UpdateGuideRequestDto updateGuideRequestDto
	) {
		updateGuideService.create(updateGuideRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Update guide Admin 수정", description = "Update guide admin 수정", tags = {"Update guide Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{updateGuideId}")
	public ResponseEntity<Void> update(
		@RequestBody @Valid UpdateGuideRequestDto updateGuideRequestDto,
		@Min(1000000000) @PathVariable Long updateGuideId
	) {
		updateGuideService.update(updateGuideId, updateGuideRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Update guide Admin 노출 수정", description = "Update guide admin 노출 수정", tags = {
		"Update guide Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{updateGuideId}/expose")
	public ResponseEntity<Void> updateExposureStatus(
		@Min(1000000000) @PathVariable Long updateGuideId,
		@RequestBody @Valid UpdateGuideExposeRequestDto updateGuideExposeRequestDto
	) {
		updateGuideService.updateExposure(updateGuideId, updateGuideExposeRequestDto.getIsExposed());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
