package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
public class CouponDeliveryHistoryResponseDto {
	@ApiModelProperty(value = "Coupon Delivery History Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Coupon Id", example = "1000000000")
	private Long couponId;

	@ApiModelProperty(value = "Event Popup Id", example = "1000000000")
	private Long eventPopupId;

	@ApiModelProperty(value = "Receiver User Id", example = "1000000000")
	private Long receiverUserId;

	@ApiModelProperty(value = "Receiver Email", example = "user@virnect.com")
	private String receiverEmail;

	@ApiModelProperty(value = "Receiver User Email Domain", example = "virnect.com")
	private String receiverEmailDomain;

	@ApiModelProperty(value = "수정한 유저의 Id", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03 11:15:30")
	private String updatedDate;

	@ApiModelProperty(value = "생성한 유저의 Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "등록 일자", example = "2022-01-03 11:15:30")
	private String createdDate;

	@QueryProjection
	public CouponDeliveryHistoryResponseDto(
		Long id, Long couponId, Long eventPopupId, Long receiverUserId, String receiverEmail,
		String receiverEmailDomain,
		Long updatedBy, ZonedDateTime updatedDate, Long createdBy, ZonedDateTime createdDate
	) {
		this.id = id;
		this.couponId = couponId;
		this.eventPopupId = eventPopupId;
		this.receiverUserId = receiverUserId;
		this.receiverEmail = receiverEmail;
		this.receiverEmailDomain = receiverEmailDomain;
		this.updatedBy = updatedBy;
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
	}
}
