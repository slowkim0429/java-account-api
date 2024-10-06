package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.AccessResultType;
import com.virnect.account.util.ZonedDateTimeUtil;

@ApiModel
@Getter
public class ControlModeAccessHistoryResponseDto {

	@ApiModelProperty(value = "Control mode access history ID", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "접근 결과", example = "SUCCEEDED")
	private AccessResultType accessResultType;

	@ApiModelProperty(value = "생성자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "등록 일자", example = "2022-01-03T10:15:30")
	private String createdDate;

	@ApiModelProperty(value = "수정자", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03T11:15:30")
	private String updatedDate;

	@QueryProjection
	public ControlModeAccessHistoryResponseDto(
		Long id, AccessResultType accessResultType, Long createdBy, ZonedDateTime createdDate, Long updatedBy,
		ZonedDateTime updatedDate
	) {
		this.id = id;
		this.accessResultType = accessResultType;
		this.createdBy = createdBy;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedBy = updatedBy;
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
	}
}
