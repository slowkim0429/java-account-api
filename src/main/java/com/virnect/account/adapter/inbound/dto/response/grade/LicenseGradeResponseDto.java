package com.virnect.account.adapter.inbound.dto.response.grade;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@RequiredArgsConstructor
public class LicenseGradeResponseDto {
	@ApiModelProperty(value = "License Grade id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "License Grade 이름", position = 1, example = "Free")
	private String name;

	@ApiModelProperty(value = "License Grade Type", position = 2, example = "FREE_PLUS")
	private LicenseGradeType gradeType;

	@ApiModelProperty(value = "License Grade 설명", position = 2, example = "Free License Grade")
	private String description;

	@ApiModelProperty(value = "License Grade 상태", position = 3, example = "REGISTER")
	private ApprovalStatus status;

	@ApiModelProperty(value = "등록 일자", position = 4, example = "2022-01-03T10:15:30")
	private String createdDate;

	@ApiModelProperty(value = "수정 일자", position = 5, example = "2022-01-03T11:15:30")
	private String updatedDate;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@QueryProjection
	public LicenseGradeResponseDto(
		Long id, LicenseGradeType gradeType, String name, String description, ApprovalStatus status,
		ZonedDateTime createdDate, ZonedDateTime updatedDate, Long createdBy, Long updatedBy
	) {
		this.id = id;
		this.name = name;
		this.gradeType = gradeType;
		this.description = description;
		this.status = status;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}
}
