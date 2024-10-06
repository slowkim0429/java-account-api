package com.virnect.account.adapter.inbound.dto.request.validate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminUserLoginRequestDto {

	@ApiModelProperty(value = "로그인 아이디", name = "email", example = "admin@squars.io")
	@Email
	@NotBlank
	private String email;

	@ApiModelProperty(value = "로그인 비밀번호", position = 1, name = "password", example = "12345678")
	@Size(min = 8, max = 20)
	private String password;

	private AdminUserLoginRequestDto(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public static AdminUserLoginRequestDto of(String email, String password) {
		return new AdminUserLoginRequestDto(email, password);
	}
}
