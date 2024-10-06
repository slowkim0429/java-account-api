package com.virnect.account.adapter.inbound.dto.request.item;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@ApiModel
@Setter
@NoArgsConstructor
public class ItemPaymentLinkRequestDto {
	@NotBlank
	@Email
	@ApiModelProperty(value = "결제 링크를 전송할 이메일", name = "email", example = "test@test.com", required = true)
	private String email;
}
