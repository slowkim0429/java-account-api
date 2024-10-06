package com.virnect.account.adapter.inbound.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ForceUpdateType;

@ApiModel
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MobileForceUpdateMinimumVersionResponseDto {

	@ApiModelProperty(value = "version", example = "1.0.0")
	private String version;

	@ApiModelProperty(value = "force update type", example = "UNUSED")
	private ForceUpdateType forceUpdateType;

	@QueryProjection
	public MobileForceUpdateMinimumVersionResponseDto(String version, ForceUpdateType forceUpdateType) {
		this.version = version;
		this.forceUpdateType = forceUpdateType;
	}
}
