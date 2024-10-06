package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.FileType;
import com.virnect.account.domain.enumclass.ServiceType;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
public class UpdateGuideResponseDto {

	@ApiModelProperty(value = "update guide id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "이름", example = "Update grade name for admin")
	private String name;

	@ApiModelProperty(value = "서비스 구분", example = "[WORKSPACE, SQUARS]")
	private ServiceType serviceType;

	@ApiModelProperty(value = "파일 구분", example = "[VIDEO, IMAGE]")
	private FileType fileType;

	@ApiModelProperty(value = "파일 url", example = "https://squars.io")
	private String fileUrl;

	@ApiModelProperty(value = "업데이트 되는 날짜", example = "2023.01.16T15:07:332")
	private String dateByUpdate;

	@ApiModelProperty(value = "제목", example = "What’s new in SQUARS!")
	private String title;

	@ApiModelProperty(value = "설명", example = "You can now create generative designs, forests, motion graphics effects, terrain, and more!")
	private String description;

	@ApiModelProperty(value = "보조 제목", example = "What's improved")
	private String subTitle;

	@ApiModelProperty(value = "보조 설명", example = "- New feature contents list ...")
	private String subDescription;

	@ApiModelProperty(value = "노출 여부", example = "true")
	private Boolean isExposed;

	@ApiModelProperty(value = "생성자", example = "0")
	private Long createdBy;

	@ApiModelProperty(value = "생성일", example = "2022-11-14T01:44:55")
	private String createdDate;

	@ApiModelProperty(value = "수정자", example = "0")
	private Long updatedBy;

	@ApiModelProperty(value = "수정일", example = "2022-11-14T01:44:55")
	private String updatedDate;

	@QueryProjection
	public UpdateGuideResponseDto(
		Long id, String name, ServiceType serviceType, FileType fileType, String fileUrl, String dateByUpdate,
		String title, String description, String subTitle, String subDescription, Boolean isExposed, Long createdBy,
		ZonedDateTime createdDate, Long updatedBy, ZonedDateTime updatedDate
	) {
		this.id = id;
		this.name = name;
		this.serviceType = serviceType;
		this.fileType = fileType;
		this.fileUrl = fileUrl;
		this.dateByUpdate = dateByUpdate;
		this.title = title;
		this.description = description;
		this.subTitle = subTitle;
		this.subDescription = subDescription;
		this.isExposed = isExposed;
		this.createdBy = createdBy;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedBy = updatedBy;
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
	}
}
