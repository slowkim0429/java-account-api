package com.virnect.account.adapter.inbound.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class ServiceTimeZoneResponseDto {
	@ApiModelProperty(value = "id", example = "10000000000")
	private Long id;

	@ApiModelProperty(value = "locale code", example = "CI")
	private String localeCode;

	@ApiModelProperty(value = "zone id", example = "Africa/Abidjan")
	private String zoneId;

	@ApiModelProperty(value = "utc offset", example = "0")
	private String utcOffset;

	@QueryProjection
	public ServiceTimeZoneResponseDto(Long id, String localeCode, String zoneId, String utcOffset) {
		this.id = id;
		this.localeCode = localeCode;
		this.zoneId = zoneId;
		this.utcOffset = utcOffset;
	}
}
