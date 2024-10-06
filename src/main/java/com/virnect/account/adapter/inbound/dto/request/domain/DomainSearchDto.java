package com.virnect.account.adapter.inbound.dto.request.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel
public class DomainSearchDto {
	@ApiModelProperty(value = "서비스 지역 코드", example = "KR")
	private Long localeId;

	@ApiModelProperty(hidden = true)
	public boolean isValid() {
		return localeId != null;
	}

	@ApiModelProperty(hidden = true)
	public String getInvalidMessage() {
		return "localeId 를 입력해 주세요";
	}

}

