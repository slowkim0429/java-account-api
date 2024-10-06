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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.account.adapter.inbound.dto.request.PageRequest;
import com.virnect.account.adapter.inbound.dto.request.product.ProductSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ProductResponseDto;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.ProductService;

@Api
@Validated
@Tag(name = "Product", description = "Product에 대한 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
	private final ProductService productService;

	@Operation(summary = "Product 조회", description = "로그인 사용자가 Product ID로 사용자 조회", tags = {"Product"})
	@ApiImplicitParam(name = "productId", dataType = "string", value = "Product ID", required = true)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{productId}")
	public ResponseEntity<ProductResponseDto> getProductById(
		@Min(1000000000) @PathVariable(name = "productId") Long productId
	) {
		ProductResponseDto responseMessage = productService.getProductById(productId);
		return ResponseEntity.ok(responseMessage);
	}

	@Operation(summary = "Product List", description = "로그인 사용자가 Product Type으로 사용자 조회", tags = {"Product"})
	@PreAuthorize("isAuthenticated()")
	@GetMapping("")
	public ResponseEntity<PageContentResponseDto<ProductResponseDto>> getProducts(
		@ModelAttribute @Valid ProductSearchDto productSearchDto,
		PageRequest pageable
	) {

		if (!productSearchDto.isValid()) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, productSearchDto.isValidMessage());
		}

		PageContentResponseDto<ProductResponseDto> responseMessage = productService.getProducts(
			productSearchDto, pageable.of());
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}
}
