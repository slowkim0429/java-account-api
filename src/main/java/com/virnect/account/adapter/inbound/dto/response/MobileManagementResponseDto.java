package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.util.ZonedDateTimeUtil;

@ApiModel
@Getter
public class MobileManagementResponseDto {

	@ApiModelProperty(value = "Mobile management ID", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "공지 메시지", example = "message")
	private String message;

	@ApiModelProperty(value = "공지 노출 여부", example = "true")
	private Boolean isExposed;

	@ApiModelProperty(value = "생성 일자", example = "2022-01-04T11:44:55")
	private String createdDate;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-04T11:44:55")
	private String updatedDate;

	@ApiModelProperty(value = "Created By User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "Updated By User Id", example = "1000000000")
	private Long updatedBy;

	@QueryProjection
	public MobileManagementResponseDto(
		Long id, String message, Boolean isExposed, ZonedDateTime createdDate, ZonedDateTime updatedDate,
		Long createdBy,
		Long updatedBy
	) {
		this.id = id;
		this.message = message;
		this.isExposed = isExposed;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}
}
