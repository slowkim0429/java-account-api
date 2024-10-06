package com.virnect.account.adapter.inbound.dto.request.coupondeliveryhistory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@ApiModel
@Setter
public class CouponDeliveryHistorySearchDto {
	@ApiModelProperty(value = "Event Popup Id", example = "1000000000")
	private Long eventPopupId;

	@ApiModelProperty(value = "Coupon Id", example = "1000000000")
	private Long couponId;

	@ApiModelProperty(value = "Receiver User Id", example = "1000000000")
	private Long receiverUserId;

	@ApiModelProperty(value = "Receiver Email", example = "user@virnect.com")
	private String receiverEmail;

	@ApiModelProperty(value = "Receiver User Email Domain", example = "virnect.com")
	private String receiverEmailDomain;
}
