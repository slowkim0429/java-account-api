package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.product.ProductSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ProductResponseDto;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.model.Product;

public interface ProductRepositoryCustom {
	Optional<Product> getProduct(Long id);

	Optional<Product> getProduct(ProductType productType);

	boolean existsProductByType(ProductType productType);

	Page<ProductResponseDto> getProductResponses(ProductSearchDto productSearchDto, Pageable pageable);
}
