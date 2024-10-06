package com.virnect.account.adapter.inbound.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.item.ItemExposureSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndGradeWithLicenseAttributesResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseGradeAndLicenseAttributesResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemResponseDto;
import com.virnect.account.port.inbound.ItemService;

@Api
@Validated
@Tag(name = "Item", description = "Item에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {
	private final ItemService itemService;

	@Operation(summary = "Item 상세 조회", description = "로그인 한 사용자가 Item 상세 조회", tags = {"Item"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@GetMapping("/{itemId}")
	public ResponseEntity<ItemAndLicenseGradeAndLicenseAttributesResponseDto> getItemAndLicenseGradeAndLicenseAttributesByItemId(
		@Min(1000000000) @PathVariable("itemId") Long itemId
	) {
		ItemAndLicenseGradeAndLicenseAttributesResponseDto responseDto = itemService.getItemAndLicenseGradeAndLicenseAttributesByItemId(
			itemId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@Operation(summary = "엔터프라이즈 Item 구매 요청 데이터 검증", description = "엔터프라이즈 Item 구매 요청 데이터 검증", tags = {"Item"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@GetMapping("/{itemId}/payment-link/verification")
	public ResponseEntity<Void> verify(
		@Min(1000000000) @PathVariable("itemId") Long itemId
	) {
		itemService.verifyItemPaymentRequestData(itemId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "부가 서비스 item id 조회", description = "사용중인 구독형 item에 정의되어 있는 부가서비스 item id 조회", tags = {"Item"})
	@PreAuthorize("hasAnyRole('ROLE_ORGANIZATION_OWNER')")
	@GetMapping("/{itemId}/attributeItemId")
	public ResponseEntity<ItemResponseDto> getAttributeItemIdByLicenseItemId(
		@Min(1000000000) @PathVariable("itemId") Long itemId
	) {
		ItemResponseDto itemResponseDto = itemService.getAttributeItemIdByLicenseItemId(itemId);
		return new ResponseEntity<>(itemResponseDto, HttpStatus.OK);
	}

	@Operation(summary = "Exposed 된 Item 목록 조회", description = "Exposed 된 Item 목록 조회", tags = {"Item"})
	@PreAuthorize("permitAll()")
	@GetMapping
	public ResponseEntity<List<ItemAndGradeWithLicenseAttributesResponseDto>> getExposedItems(
		@ModelAttribute @Valid ItemExposureSearchDto searchDto
	) {
		List<ItemAndGradeWithLicenseAttributesResponseDto> exposedItems = itemService.getExposedItems(searchDto);
		return ResponseEntity.ok(exposedItems);
	}
}
