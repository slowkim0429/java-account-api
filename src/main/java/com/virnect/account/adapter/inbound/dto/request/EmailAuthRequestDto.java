package com.virnect.account.adapter.inbound.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailAuthRequestDto {
	@NotBlank
	@Email
	@ApiModelProperty(value = "인증용 이메일", name = "email", example = "test@test.com", notes = "입력한 이메일 정보로, 해당 이메일로 인증코드 메일이 발송됩니다.", required = true)
	private String email;

	public String getLowerCaseEmail() {
		return email.toLowerCase();
	}
}
