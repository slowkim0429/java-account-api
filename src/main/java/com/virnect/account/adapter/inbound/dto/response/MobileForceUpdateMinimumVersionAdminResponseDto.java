package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.ForceUpdateType;
import com.virnect.account.util.ZonedDateTimeUtil;

@ApiModel
@Getter
public class MobileForceUpdateMinimumVersionAdminResponseDto {

	@ApiModelProperty(name = "ID", example = "1000000000")
	private Long id;

	@ApiModelProperty(name = "bundle ID", example = "com.virnect.appname")
	private String bundleId;

	@ApiModelProperty(name = "version", example = "1.0.0")
	private String version;

	@ApiModelProperty(name = "강제 업데이트 타입", example = "UNUSED")
	private ForceUpdateType forceUpdateType;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "등록일시", example = "2022-01-03 11:15:30")
	private String createdDate;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03 11:15:30")
	private String updatedDate;

	@QueryProjection
	public MobileForceUpdateMinimumVersionAdminResponseDto(
		Long id, String bundleId, String version, ForceUpdateType forceUpdateType, Long createdBy,
		ZonedDateTime createdDate,
		Long updatedBy, ZonedDateTime updatedDate
	) {
		this.id = id;
		this.bundleId = bundleId;
		this.version = version;
		this.forceUpdateType = forceUpdateType;
		this.createdBy = createdBy;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedBy = updatedBy;
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
	}
}
