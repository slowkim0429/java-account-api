package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@RequiredArgsConstructor
public class LocaleResponseDto {
	@ApiModelProperty(value = "locale id", example = "1000000000")
	private Long id;
	@ApiModelProperty(value = "locale name", position = 1, example = "Korea, Republic of")
	private String name;
	@ApiModelProperty(value = "locale code", position = 2, example = "KR")
	private String code;
	@ApiModelProperty(value = "country calling code", position = 3, example = "82")
	private Integer countryCallingCode;
	@ApiModelProperty(value = "state list", position = 4, example = "")
	private List<StateResponseDto> states;
	@ApiModelProperty(value = "region id", position = 5, example = "1000000000")
	private Long regionId;
	@ApiModelProperty(value = "region name", position = 6, example = "Asia Pacific (Seoul)")
	private String regionName;
	@ApiModelProperty(value = "region code", position = 7, example = "KR")
	private String regionCode;
	@ApiModelProperty(value = "region aws code", position = 8, example = "ap-northest-2")
	private String regionAwsCode;
	@ApiModelProperty(value = "locale create date", position = 9, example = "2022-01-03T10:15:30")
	private String createdDate;
	@ApiModelProperty(value = "locale update date", position = 10, example = "2022-01-03T10:15:30")
	private String updatedDate;

	@QueryProjection
	public LocaleResponseDto(
		Long id
		, String name
		, String code
		, Integer countryCallingCode
		, Long regionId
		, String regionName
		, String regionCode
		, String regionAwsCode
		, ZonedDateTime createdDate
		, ZonedDateTime updatedDate
	) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.countryCallingCode = countryCallingCode;
		this.regionId = regionId;
		this.regionName = regionName;
		this.regionCode = regionCode;
		this.regionAwsCode = regionAwsCode;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
	}

	public void setStates(List<StateResponseDto> states) {
		this.states = states;
	}
}
