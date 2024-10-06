package com.virnect.account.adapter.inbound.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel
public class JoinedUserStatisticsResponseDto {

	@ApiModelProperty(value = "joined user count", example = "100")
	private Long joinedUserCount;

	@QueryProjection
	public JoinedUserStatisticsResponseDto(Long joinedUserCount) {
		this.joinedUserCount = joinedUserCount;
	}
}
