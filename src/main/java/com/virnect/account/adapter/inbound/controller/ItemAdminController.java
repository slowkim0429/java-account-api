package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.item.ItemExposeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemPaymentLinkRequestDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemPaymentLinkSearchDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemRequestDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemSearchDto;
import com.virnect.account.adapter.inbound.dto.request.validate.ApprovalStatusSubset;
import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemPaymentLinkResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.port.inbound.ItemService;

@Api
@Validated
@Tag(name = "Item Admin", description = "Item에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/items")
public class ItemAdminController {
	private final ItemService itemService;

	@Operation(summary = "Item 생성", description = "admin 사용자가 Item 생성", tags = {"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping
	public ResponseEntity<Void> create(
		@RequestBody @Valid ItemRequestDto requestDto
	) {
		itemService.create(requestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "item 수정", description = "admin 사용자가 item 수정", tags = {"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{itemId}")
	public ResponseEntity<Void> update(
		@RequestBody @Valid ItemRequestDto itemRequestDto,
		@Min(1000000000) @PathVariable("itemId") Long itemId
	) {
		itemService.update(itemId, itemRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Item 상태 변경", description = "Admin 사용자가 Item 상태 변경", tags = {"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{itemId}/status/{status}")
	public ResponseEntity<Void> updateByStatus(
		@Min(1000000000) @PathVariable("itemId") Long itemId,
		@NotBlank @ApprovalStatusSubset(anyOf = {ApprovalStatus.APPROVED, ApprovalStatus.REJECT})
		@PathVariable("status") String status
	) {
		itemService.updateByStatus(itemId, ApprovalStatus.valueOf(status));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Item 상세 조회", description = "Admin 사용자가 Item 상세 조회", tags = {"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{itemId}")
	public ResponseEntity<ItemDetailResponseDto> getItemDetailById(
		@Min(1000000000) @PathVariable("itemId") Long itemId
	) {
		ItemDetailResponseDto responseDto = itemService.getItemDetailById(itemId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@Operation(summary = "Item 변경 이력 조회", description = "Admin 사용자가 Item ID로 변경 이력 조회", tags = {"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/{itemId}/revisions")
	public ResponseEntity<PageContentResponseDto<ItemRevisionResponseDto>> getItemRevisions(
		@Min(1000000000) @PathVariable("itemId") Long itemId,
		PageRequest pageRequest
	) {
		PageContentResponseDto<ItemRevisionResponseDto> responseDto = itemService.getItemRevisions(
			itemId, pageRequest.of());
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@Operation(summary = "Item 삭제", description = "Item 상태 delete로 변경", tags = {"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{itemId}/useStatus/delete")
	public ResponseEntity<Void> delete(
		@Min(1000000000) @PathVariable("itemId") Long itemId
	) {
		itemService.delete(itemId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Item List 조회", description = "Admin 사용자가 Item List 정보를 조회", tags = {"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<ItemAndLicenseResponseDto>> getItemList(
		@ModelAttribute @Valid ItemSearchDto itemSearchDto,
		PageRequest pageRequest
	) {
		PageContentResponseDto<ItemAndLicenseResponseDto> itemList = itemService.getItemList(
			itemSearchDto, pageRequest.of());
		return ResponseEntity.ok(itemList);
	}

	@Operation(summary = "Item 구매 링크 전송", description = "Admin 사용자가 Item 구매 링크를 전송합니다.", tags = {"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping("/{itemId}/payment-link")
	public ResponseEntity<Void> createItemPaymentLink(
		@Min(1000000000) @PathVariable("itemId") Long itemId,
		@RequestBody @Valid ItemPaymentLinkRequestDto itemPaymentLinkRequestDto
	) {
		itemService.createItemPaymentLink(itemId, itemPaymentLinkRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Item 의 isExposed 상태를 toggle", description = "Admin 사용자가 Item 의 노출여부를 설정합니다", tags = {
		"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PutMapping("/{itemId}/expose")
	public ResponseEntity<Void> changeItemExposurable(
		@Min(1000000000) @PathVariable Long itemId,
		@RequestBody @Valid ItemExposeRequestDto itemExposeRequestDto
	) {
		itemService.updateByExpose(itemId, itemExposeRequestDto.getIsExposed());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Item data synchronize", description = "Item 정보 전파", tags = {"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@PostMapping("/{itemId}/synchronize")
	public ResponseEntity<Void> synchronizeItem(@Min(1000000000) @PathVariable Long itemId) {
		itemService.synchronizeItem(itemId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Item 구매 링크 전송 이력 조회", description = "Admin 사용자가 Item 구매 링크를 전송한 이력을 조회합니다.", tags = {
		"Item Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER', 'ROLE_ADMIN_MASTER')")
	@GetMapping("/payment-links")
	public ResponseEntity<PageContentResponseDto<ItemPaymentLinkResponseDto>> getItemPaymentLinks(
		@ModelAttribute ItemPaymentLinkSearchDto itemPaymentLinkSearchDto, PageRequest pageRequest
	) {
		PageContentResponseDto<ItemPaymentLinkResponseDto> responseDto = itemService.getItemPaymentLinks(
			itemPaymentLinkSearchDto, pageRequest.of());
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
