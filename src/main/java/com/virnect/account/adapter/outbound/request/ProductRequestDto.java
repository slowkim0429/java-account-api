package com.virnect.account.adapter.outbound.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRequestDto {
	@JsonProperty(value = "description")
	private String description;

	@JsonProperty(value = "hs_cost_of_goods_sold")
	private BigDecimal hsCostOfGoodsSold;

	@JsonProperty(value = "hs_sku")
	private Long itemId;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "hs_product_type")
	private String productType = "STUDIO";

	@JsonProperty(value = "hs_price_eur")
	private BigDecimal price;

	@JsonProperty(value = "item_type")
	private String itemType;

	@JsonProperty(value = "recurringbillingfrequency")
	private String recurringInterval;

	@Builder
	public ProductRequestDto(
		String description, BigDecimal hsCostOfGoodsSold, Long itemId, String name, BigDecimal price,
		String itemType, String recurringInterval
	) {
		this.description = description;
		this.hsCostOfGoodsSold = hsCostOfGoodsSold;
		this.itemId = itemId;
		this.name = name;
		this.price = price;
		this.itemType = itemType;
		this.recurringInterval = recurringInterval;
	}

	@Override
	public String toString() {
		return "ProductRequestDto{" +
			"description='" + description + '\'' +
			", hsCostOfGoodsSold=" + hsCostOfGoodsSold +
			", itemId=" + itemId +
			", name='" + name + '\'' +
			", productType='" + productType + '\'' +
			", price=" + price +
			", itemType='" + itemType + '\'' +
			", recurringInterval='" + recurringInterval + '\'' +
			'}';
	}
}
