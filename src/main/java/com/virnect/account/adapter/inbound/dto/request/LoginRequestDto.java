package com.virnect.account.adapter.inbound.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LoginRequestDto {

	@ApiModelProperty(value = "로그인 아이디", name = "email", example = "admin@squars.io")
	@Email
	@NotBlank
	private String email;

	@ApiModelProperty(value = "로그인 비밀번호", position = 1, name = "password", example = "12345678")
	@Size(min = 8, max = 20)
	private String password;
}
