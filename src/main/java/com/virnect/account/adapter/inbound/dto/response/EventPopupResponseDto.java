package com.virnect.account.adapter.inbound.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;
import com.virnect.account.domain.enumclass.ExposureOptionType;

@ApiModel
@Getter
@Setter
public class EventPopupResponseDto {

	@ApiModelProperty(value = "id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "name", example = "this name")
	private String name;

	@ApiModelProperty(value = "eventType", example = "MARKETING")
	private EventType eventType;

	@ApiModelProperty(value = "serviceType", example = "SQUARS")
	private EventServiceType serviceType;

	@ApiModelProperty(value = "imageUrl", example = "https://squars.io")
	private String imageUrl;

	@ApiModelProperty(value = "imageLinkUrl", example = "https://squars.io")
	private String imageLinkUrl;

	@ApiModelProperty(value = "contentDescription", example = "<h1>Example</h1>")
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

	@ApiModelProperty(value = "inputGuide", example = "Please enter your email")
	private String inputGuide;

	@ApiModelProperty(value = "emailTitle", example = "Email")
	private String emailTitle;

	@ApiModelProperty(value = "emailImageUrl", example = "https://squars.io")
	private String emailImageUrl;

	@QueryProjection
	public EventPopupResponseDto(
		Long id, String name, EventType eventType, EventServiceType serviceType, String imageUrl, String imageLinkUrl,
		String contentDescription, String buttonLabel, String buttonUrl, ExposureOptionType exposureOptionType,
		DataType exposureOptionDataType, String exposureOptionValue, Boolean isExposed, Long couponId,
		String inputGuide,
		String emailTitle, String emailImageUrl
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
		this.emailImageUrl = emailImageUrl;
	}
}
