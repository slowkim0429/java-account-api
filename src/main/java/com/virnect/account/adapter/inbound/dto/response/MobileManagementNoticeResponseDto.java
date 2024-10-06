package com.virnect.account.adapter.inbound.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MobileManagementNoticeResponseDto {

	@ApiModelProperty(value = "notice message", example = "4월 4일 오후 2시 부터 긴급 점검에 들어갑니다.")
	private String message;

	@QueryProjection
	public MobileManagementNoticeResponseDto(String message) {
		this.message = message;
	}
}
