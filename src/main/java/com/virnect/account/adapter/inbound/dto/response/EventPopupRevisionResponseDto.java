package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;
import com.virnect.account.domain.enumclass.ExposureOptionType;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.model.EventPopup;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class EventPopupRevisionResponseDto {

	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

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

	public EventPopupRevisionResponseDto(
		RevisionType revisionType, EventPopup eventPopup
	) {
		this.revisionType = revisionType;
		this.id = eventPopup.getId();
		this.name = eventPopup.getName();
		this.eventType = eventPopup.getEventType();
		this.serviceType = eventPopup.getServiceType();
		this.imageUrl = eventPopup.getImageUrl();
		this.imageLinkUrl = eventPopup.getImageLinkUrl();
		this.contentDescription = eventPopup.getContentDescription();
		this.buttonLabel = eventPopup.getButtonLabel();
		this.buttonUrl = eventPopup.getButtonUrl();
		this.exposureOptionType = eventPopup.getExposureOptionType();
		this.exposureOptionDataType = eventPopup.getExposureOptionDataType();
		this.exposureOptionValue = eventPopup.getExposureOptionValue();
		this.isExposed = eventPopup.getIsExposed();
		this.couponId = eventPopup.getCouponId();
		this.inputGuide = eventPopup.getInputGuide();
		this.emailTitle = eventPopup.getEmailTitle();
		this.emailContentInlineImageUrl = eventPopup.getEmailContentInlineImageUrl();
		this.createdBy = eventPopup.getCreatedBy();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(eventPopup.getCreatedDate());
		this.updatedBy = eventPopup.getLastModifiedBy();
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(eventPopup.getUpdatedDate());
	}

	public static EventPopupRevisionResponseDto of(Byte representation, EventPopup eventPopup) {
		return new EventPopupRevisionResponseDto(RevisionType.valueOf(representation), eventPopup);
	}
}
