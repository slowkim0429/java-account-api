package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
public class DomainResponseDto {
	@ApiModelProperty(value = "domain id", example = "1000000000")
	private final Long id;
	@ApiModelProperty(value = "domain record name", position = 1, example = "HOME")
	private final String name;
	@ApiModelProperty(value = "url", position = 2, example = "https://10.200.0.24:3000")
	private final String url;
	@ApiModelProperty(value = "region id", position = 3, example = "1000000000")
	private final Long regionId;
	@ApiModelProperty(value = "domain create date", position = 4, example = "2022-01-03T10:15:30")
	private final String createdDate;

	@QueryProjection
	public DomainResponseDto(
		Long id
		, String name
		, String url
		, Long regionId
		, ZonedDateTime createdDate
	) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.regionId = regionId;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
	}
}
