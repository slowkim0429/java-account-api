package com.virnect.account.adapter.inbound.dto.request.password;

import java.util.Locale;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel
public class EmailChangePasswordRequestDto {
	@NotBlank
	@Email
	@ApiModelProperty(value = "비밀번호 변경 용 이메일", name = "email", example = "test@test.com")
	private String email;

	public String getLowerCaseEmail() {
		return email.toLowerCase();
	}
}
