package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@ApiModel
@Getter
public class EmailManagementResponseDto {

	@ApiModelProperty(value = "Email Management Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Email Type", example = "WELCOME")
	private Mail emailType;

	@ApiModelProperty(value = "Email Contents Inline Image Url", example = "https://devfile.squars.io/virnect-platform/.....png")
	private String contentsInlineImageUrl;

	@ApiModelProperty(value = "Description", example = "sign up email")
	private String description;

	@ApiModelProperty(value = "Use Status", example = "UNUSE")
	private UseStatus useStatus;

	@ApiModelProperty(value = "Created Date", example = "2022-01-04T11:44:55")
	private String createdDate;

	@ApiModelProperty(value = "Updated Date", example = "2022-01-04T11:44:55")
	private String updatedDate;

	@ApiModelProperty(value = "Created By User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "Updated By User Id", example = "1000000000")
	private Long updatedBy;

	@QueryProjection
	public EmailManagementResponseDto(
		Long id, Mail emailType, String contentsInlineImageUrl, String description, UseStatus useStatus,
		ZonedDateTime createdDate,
		ZonedDateTime updatedDate, Long createdBy, Long updatedBy
	) {
		this.id = id;
		this.emailType = emailType;
		this.contentsInlineImageUrl = contentsInlineImageUrl;
		this.description = description;
		this.useStatus = useStatus;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}
}
