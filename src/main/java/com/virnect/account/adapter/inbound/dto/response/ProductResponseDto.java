package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.model.Product;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
public class ProductResponseDto {
	@ApiModelProperty(value = "product id", example = "1000000000")
	private final Long id;
	@ApiModelProperty(value = "product 이름", position = 1, example = "리모트")
	private final String name;
	@ApiModelProperty(value = "product 타입(service)", position = 2, example = "SQUARS")
	private final ProductType productType;
	@ApiModelProperty(value = "product 상태", position = 3, example = "APPROVED")
	private final ApprovalStatus status;
	@ApiModelProperty(value = "product 생성 일자", position = 4, example = "2022-01-03T10:15:30")
	private final String createdDate;
	@ApiModelProperty(value = "product 생성 일자", position = 5, example = "2022-01-03T10:15:30")
	private final String updatedDate;

	@QueryProjection
	public ProductResponseDto(
		Long id
		, String name
		, ProductType productType
		, ApprovalStatus status
		, ZonedDateTime createdDate
		, ZonedDateTime updatedDate
	) {
		this.id = id;
		this.name = name;
		this.productType = productType;
		this.status = status;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
	}

	public ProductResponseDto(Product product) {
		this.id = product.getId();
		this.name = product.getName();
		this.productType = product.getProductType();
		this.status = product.getStatus();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(product.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(product.getUpdatedDate());
	}
}
