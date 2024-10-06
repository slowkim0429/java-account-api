package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;
import com.virnect.account.domain.enumclass.ExposureOptionType;
import com.virnect.account.util.ZonedDateTimeUtil;

@ApiModel
@Getter
@Setter
public class EventPopupAdminResponseDto {

	@ApiModelProperty(value = "id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "name", example = "test")
	private String name;

	@ApiModelProperty(value = "eventType", example = "MARKETING")
	private EventType eventType;

	@ApiModelProperty(value = "serviceType", example = "SQUARS")
	private EventServiceType serviceType;

	@ApiModelProperty(value = "imageUrl", example = "https://image.path")
	private String imageUrl;

	@ApiModelProperty(value = "imageLinkUrl", example = "https://squars.io")
	private String imageLinkUrl;

	@ApiModelProperty(value = "contentDescription", example = "<h1>content description</h1>")
	private String contentDescription;

	@ApiModelProperty(value = "buttonLabel", example = "Submit")
	private String buttonLabel;

	@ApiModelProperty(value = "buttonUrl", example = "https://squars.io")
	private String buttonUrl;

	@ApiModelProperty(value = "exposureOptionType", example = "SELECTIVE_DEACTIVATION_DAY")
	private ExposureOptionType exposureOptionType;

	@ApiModelProperty(value = "exposureOptionDataType", example = "NUMBER")
	private DataType exposureOptionDataType;

	@ApiModelProperty(value = "exposureOptionValue", example = "3")
	private String exposureOptionValue;

	@ApiModelProperty(value = "isExposed", example = "true")
	private Boolean isExposed;

	@ApiModelProperty(value = "couponId", example = "1000000000")
	private Long couponId;

	@ApiModelProperty(value = "inputGuide", example = "Please input your email")
	private String inputGuide;

	@ApiModelProperty(value = "emailTitle", example = "hello!")
	private String emailTitle;

	@ApiModelProperty(value = "emailContentInlineImageUrl", example = "https://image.path")
	private String emailContentInlineImageUrl;

	@ApiModelProperty(value = "생성자", example = "0")
	private Long createdBy;

	@ApiModelProperty(value = "생성일", example = "2022-11-14T01:44:55")
	private String createdDate;

	@ApiModelProperty(value = "수정자", example = "0")
	private Long updatedBy;

	@ApiModelProperty(value = "수정일", example = "2022-11-14T01:44:55")
	private String updatedDate;

	@QueryProjection
	public EventPopupAdminResponseDto(
		Long id, String name, EventType eventType, EventServiceType serviceType, String imageUrl, String imageLinkUrl,
		String contentDescription, String buttonLabel, String buttonUrl, ExposureOptionType exposureOptionType,
		DataType exposureOptionDataType, String exposureOptionValue, Boolean isExposed, Long couponId,
		String inputGuide,
		String emailTitle, String emailContentInlineImageUrl, Long createdBy, ZonedDateTime createdDate, Long updatedBy,
		ZonedDateTime updatedDate
	) {
		this.id = id;
		this.name = name;
		this.eventType = eventType;
		this.serviceType = serviceType;
		this.imageUrl = imageUrl;
		this.imageLinkUrl = imageLinkUrl;
		this.contentDescription = contentDescription;
		this.buttonLabel = buttonLabel;
		this.buttonUrl = buttonUrl;
		this.exposureOptionType = exposureOptionType;
		this.exposureOptionDataType = exposureOptionDataType;
		this.exposureOptionValue = exposureOptionValue;
		this.isExposed = isExposed;
		this.couponId = couponId;
		this.inputGuide = inputGuide;
		this.emailTitle = emailTitle;
		this.emailContentInlineImageUrl = emailContentInlineImageUrl;
		this.createdBy = createdBy;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedBy = updatedBy;
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
	}
}
