package com.virnect.account.adapter.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HubSpotProductRequestDto {
	@JsonProperty(value = "properties")
	private ProductRequestDto productRequestDto;

	public HubSpotProductRequestDto(ProductRequestDto productRequestDto) {
		this.productRequestDto = productRequestDto;
	}

	@Override
	public String toString() {
		return "HubSpotProductRequestDto{" +
			"productRequestDto=" + productRequestDto +
			'}';
	}
}
