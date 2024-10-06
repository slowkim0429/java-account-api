package com.virnect.account.adapter.inbound.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StateResponseDto {
	@ApiModelProperty(value = "Service Locale State Id", example = "1000000000")
	private Long id;
	@ApiModelProperty(value = "Service Locale State Name", position = 1, example = "Alabama")
	private String name;
	@ApiModelProperty(value = "Service Locale State Code", position = 2, example = "US-AL")
	private String code;

	@QueryProjection
	public StateResponseDto(Long id, String name, String code) {
		this.id = id;
		this.name = name;
		this.code = code;
	}
}
