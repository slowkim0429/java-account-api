package com.virnect.account.adapter.inbound.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UpdatePasswordRequestDto {
	@ApiModelProperty(value = "이메일", example = "admin@squars.io")
	@NotBlank
	@Email
	private String email;

	@ApiModelProperty(value = "인증 코드", example = "123456")
	@NotBlank
	@Pattern(regexp = "[0-9]{6}")
	private String code;

	@ApiModelProperty(value = "새 비밀번호", example = "qwer123@")
	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[,./!@#$%])[A-Za-z\\d,./!@#$%]{8,20}$")
	private String newPassword;

	@ApiModelProperty(value = "새 비밀번호 체크", example = "qwer123@")
	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[,./!@#$%])[A-Za-z\\d,./!@#$%]{8,20}$")
	private String checkPassword;
}
