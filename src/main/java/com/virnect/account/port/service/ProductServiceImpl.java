package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.product.ProductCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductSearchDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductStatusChangeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ProductResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.model.Product;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.ProductService;
import com.virnect.account.port.outbound.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final ProductRepository productRepository;

	@Transactional
	public void create(ProductCreateRequestDto productCreateRequestDto) {
		if (productRepository.existsProductByType(productCreateRequestDto.getProductType())) {
			throw new CustomException(DUPLICATE_PRODUCT);
		}

		Product newProduct = Product.builder()
			.productType(productCreateRequestDto.getProductType())
			.name(productCreateRequestDto.getName())
			.build();

		productRepository.save(newProduct);
	}

	@Transactional
	public void update(ProductUpdateRequestDto productUpdateRequestDto) {
		productRepository.getProduct(productUpdateRequestDto.getProductType())
			.ifPresentOrElse(
				product -> {
					if (!product.getId().equals(productUpdateRequestDto.getId())) {
						throw new CustomException(DUPLICATE_PRODUCT);
					}

					if (product.getStatus().equals(ApprovalStatus.APPROVED) ||
						product.getStatus().equals(ApprovalStatus.REJECT)) {
						throw new CustomException(INVALID_PRODUCT);
					}

					Product changedProduct = Product.updateProduct(
						productUpdateRequestDto.getName(),
						product
					);

					productRepository.save(changedProduct);
				},
				() -> {
					Product newProduct = Product.builder()
						.productType(productUpdateRequestDto.getProductType())
						.name(productUpdateRequestDto.getName())
						.build();
					productRepository.save(newProduct);
				}
			);
	}

	@Transactional
	public void updateByStatus(ProductStatusChangeRequestDto productStatusChangeRequestDto) {
		Product product = productRepository.getProduct(productStatusChangeRequestDto.getProductId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

		if (product.getStatus() != productStatusChangeRequestDto.getStatus()) {
			product.setStatus(productStatusChangeRequestDto.getStatus());
			productRepository.save(product);
		}
	}

	@Transactional(readOnly = true)
	public ProductResponseDto getProductById(Long productId) {
		return productRepository.getProduct(productId)
			.map(ProductResponseDto::new)
			.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));
	}

	@Transactional(readOnly = true)
	public PageContentResponseDto<ProductResponseDto> getProducts(
		ProductSearchDto productSearchDto, Pageable pageable
	) {
		Page<ProductResponseDto> products = productRepository.getProductResponses(productSearchDto, pageable);
		return new PageContentResponseDto<>(products, pageable);
	}
}
