package com.virnect.account.adapter.inbound.dto.request.eventpopup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@ApiModel
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SendCouponRequestDto {
	@ApiModelProperty(value = "이메일", example = "test@test.com", required = true)
	@NotBlank(message = "쿠폰 전송할 이메일은 반드시 입력되어야 합니다.")
	@Email
	private String email;

	public SendCouponRequestDto(String email) {
		this.email = email;
	}
}
