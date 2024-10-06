package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupUpdateRequestDto;
import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;
import com.virnect.account.domain.enumclass.ExposureOptionType;

@Entity
@Getter
@Audited
@Table(name = "event_popups", indexes = {
	@Index(name = "idx_name", columnList = "name"),
	@Index(name = "idx_coupon_id", columnList = "coupon_id"),
	@Index(name = "idx_event_type", columnList = "event_type"),
	@Index(name = "idx_service_type", columnList = "service_type"),
	@Index(name = "idx_is_exposed", columnList = "is_exposed"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventPopup extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "event_type", length = 50, nullable = false)
	private EventType eventType;

	@Enumerated(EnumType.STRING)
	@Column(name = "service_type", length = 50, nullable = false)
	private EventServiceType serviceType;

	@Setter
	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@Column(name = "image_link_url")
	private String imageLinkUrl;

	@Lob
	@Column(name = "content_description")
	private String contentDescription;

	@Column(name = "button_label", length = 30)
	private String buttonLabel;

	@Column(name = "button_url")
	private String buttonUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "exposure_option_type", length = 50, nullable = false)
	private ExposureOptionType exposureOptionType;

	@Enumerated(EnumType.STRING)
	@Column(name = "exposure_option_data_type", length = 50)
	private DataType exposureOptionDataType;

	@Column(name = "exposure_option_value", length = 50)
	private String exposureOptionValue;

	@Setter
	@Column(name = "is_exposed", nullable = false)
	private Boolean isExposed;

	@Column(name = "coupon_id")
	private Long couponId;

	@Column(name = "input_guide")
	private String inputGuide;

	@Column(name = "email_title", length = 50)
	private String emailTitle;

	@Setter
	@Column(name = "email_content_inline_image_url")
	private String emailContentInlineImageUrl;

	public EventPopup(
		String name, EventType eventType, EventServiceType serviceType, String imageUrl, String imageLinkUrl,
		String contentDescription, String buttonLabel, String buttonUrl, ExposureOptionType exposureOptionType,
		DataType exposureOptionDataType, String exposureOptionValue, Boolean isExposed, Long couponId,
		String inputGuide,
		String emailTitle, String emailContentInlineImageUrl
	) {
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
	}

	public static EventPopup of(
		EventPopupCreateRequestDto createRequestDto, String imageUrl, String emailContentInlineImageUrl
	) {
		return new EventPopup(
			createRequestDto.getName(),
			createRequestDto.getEventType(),
			createRequestDto.getServiceType(),
			imageUrl,
			createRequestDto.getImageLinkUrl(),
			createRequestDto.getContentDescription(),
			createRequestDto.getButtonLabel(),
			createRequestDto.getButtonUrl(),
			createRequestDto.getExposureOptionType(),
			createRequestDto.getExposureOptionDataType(),
			createRequestDto.getExposureOptionValue(),
			false,
			createRequestDto.getCouponId(),
			createRequestDto.getInputGuide(),
			createRequestDto.getEmailTitle(),
			emailContentInlineImageUrl
		);
	}

	public void update(EventPopupUpdateRequestDto eventPopupUpdateRequestDto) {
		this.name = eventPopupUpdateRequestDto.getName();
		this.imageLinkUrl = eventPopupUpdateRequestDto.getImageLinkUrl();
		this.contentDescription = eventPopupUpdateRequestDto.getContentDescription();
		this.buttonLabel = eventPopupUpdateRequestDto.getButtonLabel();
		this.buttonUrl = eventPopupUpdateRequestDto.getButtonUrl();
		this.exposureOptionType = eventPopupUpdateRequestDto.getExposureOptionType();
		this.exposureOptionDataType = eventPopupUpdateRequestDto.getExposureOptionDataType();
		this.exposureOptionValue = eventPopupUpdateRequestDto.getExposureOptionValue();
		this.inputGuide = eventPopupUpdateRequestDto.getInputGuide();
		this.emailTitle = eventPopupUpdateRequestDto.getEmailTitle();
	}
}
