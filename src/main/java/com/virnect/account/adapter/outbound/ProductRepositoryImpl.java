package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QProduct.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.product.ProductSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ProductResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QProductResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.model.Product;
import com.virnect.account.port.outbound.ProductRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {
	private final JPAQueryFactory query;

	private BooleanExpression eqId(Long id) {
		if (id == null || id <= 0) {
			return null;
		}
		return product.id.eq(id);
	}

	private BooleanExpression eqProductType(ProductType productType) {
		if (productType == null) {
			return null;
		}
		return product.productType.eq(productType);
	}

	private BooleanExpression eqStatus(ApprovalStatus status) {
		if (status == null) {
			return null;
		}

		return product.status.eq(status);
	}

	@Override
	public Optional<Product> getProduct(Long id) {
		return Optional.ofNullable(
			query.selectFrom(product)
				.where(
					eqId(id)
				)
				.fetchOne());
	}

	@Override
	public Optional<Product> getProduct(ProductType productType) {
		return Optional.ofNullable(
			query.selectFrom(product)
				.where(
					product.productType.eq(productType)
				)
				.fetchOne()
		);
	}

	@Override
	public boolean existsProductByType(ProductType productType) {
		Integer productId = query.selectOne().from(product)
			.where(
				eqProductType(productType),
				product.status.ne(ApprovalStatus.REJECT)
			).fetchFirst();
		return productId != null;
	}

	@Override
	public Page<ProductResponseDto> getProductResponses(ProductSearchDto productSearchDto, Pageable pageable) {
		QueryResults<ProductResponseDto> pagingResult = query
			.select(new QProductResponseDto(
					product.id
					, product.name
					, product.productType
					, product.status
					, product.createdDate
					, product.updatedDate
				)
			)
			.from(product)
			.where(
				eqProductType(productSearchDto.getProductType()),
				eqStatus(productSearchDto.getStatus())
			)
			.orderBy(product.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}
}
