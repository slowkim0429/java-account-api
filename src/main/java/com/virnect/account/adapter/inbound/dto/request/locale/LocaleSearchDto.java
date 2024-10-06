package com.virnect.account.adapter.inbound.dto.request.locale;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel
public class LocaleSearchDto {
	@ApiModelProperty(value = "서비스 지역 코드", example = "KR")
	private String code;

	@ApiModelProperty(value = "서비스 리전 코드", example = "KR")
	private String regionCode;
}

