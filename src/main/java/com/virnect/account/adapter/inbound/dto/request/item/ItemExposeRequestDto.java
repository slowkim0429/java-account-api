package com.virnect.account.adapter.inbound.dto.request.item;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@ApiModel
@NoArgsConstructor
public class ItemExposeRequestDto {

	@ApiModelProperty(value = "노출여부", example = "true", required = true)
	@NotNull
	private Boolean isExposed;

	public ItemExposeRequestDto(Boolean isExposed) {
		this.isExposed = isExposed;
	}
}
