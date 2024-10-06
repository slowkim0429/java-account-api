package com.virnect.account.adapter.inbound.dto.request.eventpopup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;

@ApiModel
@Getter
@Setter
public class EventPopupSearchDto {

	@ApiModelProperty(value = "id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "name", example = "TEST")
	private String name;

	@ApiModelProperty(value = "coupon id", example = "1000000000")
	private Long couponId;

	@ApiModelProperty(value = "Event type", example = "SUBMISSION")
	@CommonEnum(enumClass = EventType.class)
	private String eventType;

	@ApiModelProperty(value = "Service type", example = "SQUARS")
	@CommonEnum(enumClass = EventServiceType.class)
	private String serviceType;

	@ApiModelProperty(value = "Expose", example = "true")
	private Boolean isExposed;
}
