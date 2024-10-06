package com.virnect.account.adapter.inbound.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.product.ProductCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductSearchDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductStatusChangeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ProductResponseDto;
import com.virnect.account.log.NoLogging;
import com.virnect.account.port.inbound.ProductService;

@Api
@Validated
@Tag(name = "Product Admin", description = "Product에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class ProductAdminController {
	private final ProductService productService;

	@Operation(summary = "Product 등록", description = "admin 사용자가 Product 등록", tags = {"Product Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER')")
	@PostMapping
	public ResponseEntity<Void> create(
		@RequestBody @Valid ProductCreateRequestDto productCreateRequestDto
	) {
		productService.create(productCreateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Product 변경", description = "admin 사용자가 Product 변경", tags = {"Product Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER')")
	@PutMapping
	public ResponseEntity<Void> update(
		@RequestBody @Valid ProductUpdateRequestDto productUpdateRequestDto
	) {
		productService.update(productUpdateRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Product 상태변경", description = "Product 상태변경", tags = {"Product Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER')")
	@PutMapping("/status")
	@NoLogging
	public ResponseEntity<Void> updateByStatus(
		@RequestBody @Valid ProductStatusChangeRequestDto productStatusChangeRequestDto
	) {
		productService.updateByStatus(productStatusChangeRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "Product 조회", description = "admin 사용자가 Product ID로 사용자 조회", tags = {"Product Admin"})
	@ApiImplicitParam(name = "productId", dataType = "string", value = "Product ID", required = true)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER')")
	@GetMapping("/{productId}")
	public ResponseEntity<ProductResponseDto> getProductById(
		@Min(1000000000) @PathVariable(name = "productId") Long productId
	) {
		ProductResponseDto responseMessage = productService.getProductById(productId);
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "Product List", description = "admin 사용자가 Product Type으로 사용자 조회", tags = {"Product Admin"})
	@PreAuthorize("hasAnyRole('ROLE_ADMIN_USER')")
	@GetMapping
	public ResponseEntity<PageContentResponseDto<ProductResponseDto>> getProducts(
		@ModelAttribute @Valid ProductSearchDto productSearchDto,
		PageRequest pageable
	) {
		PageContentResponseDto<ProductResponseDto> responseMessage = productService.getProducts(
			productSearchDto, pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}
}
