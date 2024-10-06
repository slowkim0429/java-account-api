package com.virnect.account.adapter.inbound.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.PasswordPolicy;

@Getter
@Setter
@ApiModel
public class PasswordVerificationRequestDto {
	@ApiModelProperty(value = "비밀번호")
	@NotBlank
	@PasswordPolicy(minLength = 8, maxLength = 20, maxNumberOfRepeatedLetter = 3)
	private String password;
}
