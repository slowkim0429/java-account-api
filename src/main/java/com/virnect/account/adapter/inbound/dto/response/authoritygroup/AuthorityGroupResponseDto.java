package com.virnect.account.adapter.inbound.dto.response.authoritygroup;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@ApiModel
@Getter
public class AuthorityGroupResponseDto {
	@ApiModelProperty(value = "id", example = "10000000000")
	private Long id;

	@ApiModelProperty(value = "name", example = "Admin master")
	private String name;

	@ApiModelProperty(value = "description", example = "For admin master")
	private String description;

	@ApiModelProperty(value = "status", example = "USE")
	private UseStatus status;

	@ApiModelProperty(value = "생성자", example = "10000000000")
	private Long createdBy;

	@ApiModelProperty(value = "생성일", example = "2022-11-14 01:44:55")
	private String createdDate;

	@ApiModelProperty(value = "수정자", example = "10000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정일", example = "2022-11-14 01:44:55")
	private String updatedDate;

	@QueryProjection
	public AuthorityGroupResponseDto(
		Long id, String name, String description, UseStatus status, Long createdBy, ZonedDateTime createdDate,
		Long updatedBy,
		ZonedDateTime updatedDate
	) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.status = status;
		this.createdBy = createdBy;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedBy = updatedBy;
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
	}
}
