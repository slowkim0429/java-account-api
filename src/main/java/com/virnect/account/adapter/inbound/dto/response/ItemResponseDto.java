package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel
public class ItemResponseDto {
	@ApiModelProperty(value = "item id", example = "1000000000")
	private Long id;

	private ItemResponseDto(Long id) {
		this.id = id;
	}

	public static ItemResponseDto from(Long attributeItemId) {
		return new ItemResponseDto(attributeItemId);
	}
}
