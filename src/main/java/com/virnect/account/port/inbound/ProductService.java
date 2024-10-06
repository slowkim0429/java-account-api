package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.product.ProductCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductSearchDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductStatusChangeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ProductResponseDto;

public interface ProductService {
	void create(ProductCreateRequestDto productCreateRequestDto);

	void update(ProductUpdateRequestDto productUpdateRequestDto);

	void updateByStatus(ProductStatusChangeRequestDto productStatusChangeRequestDto);

	ProductResponseDto getProductById(Long id);

	PageContentResponseDto<ProductResponseDto> getProducts(ProductSearchDto productSearchDto, Pageable pageable);
}
