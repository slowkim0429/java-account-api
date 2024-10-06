package com.virnect.account.adapter.inbound.dto.request.eventpopup;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel
@Getter
@NoArgsConstructor
public class EventPopupExposeRequestDto {

	@ApiModelProperty(value = "노출여부", example = "true", required = true)
	@NotNull
	private Boolean isExposed;

	public EventPopupExposeRequestDto(Boolean isExposed) {
		this.isExposed = isExposed;
	}
}
