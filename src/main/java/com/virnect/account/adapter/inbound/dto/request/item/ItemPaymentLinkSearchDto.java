package com.virnect.account.adapter.inbound.dto.request.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@ApiModel
@Setter
public class ItemPaymentLinkSearchDto {
	@ApiModelProperty(value = "Item Id", example = "1000000000")
	private Long itemId;

	@ApiModelProperty(value = "User Id", example = "1000000000")
	private Long userId;

	@ApiModelProperty(value = "Email", example = "ljk@virnect.com")
	private String email;

	@ApiModelProperty(value = "Email Domain", example = "virnect.com")
	private String emailDomain;
}
