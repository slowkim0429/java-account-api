package com.virnect.account.adapter.inbound.dto.request.updateguide;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@ApiModel
@NoArgsConstructor
public class UpdateGuideExposeRequestDto {

	@ApiModelProperty(value = "노출여부", example = "true", required = true)
	@NotNull(message = "isExposed 값은 null 일 수 없습니다.")
	private Boolean isExposed;

	public UpdateGuideExposeRequestDto(Boolean isExposed) {
		this.isExposed = isExposed;
	}
}
