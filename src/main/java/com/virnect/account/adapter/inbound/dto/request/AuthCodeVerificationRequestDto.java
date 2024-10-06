package com.virnect.account.adapter.inbound.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class AuthCodeVerificationRequestDto {
	@NotBlank
	@Email
	private String email;
	@NotBlank
	@Pattern(regexp = "[0-9]{6}")
	private String code;
}
