package com.virnect.account.adapter.inbound.dto.request.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdatePasswordRequestDto {
	@ApiModelProperty(value = "현재비밀번호", required = true, example = "admin12!")
	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[,./!@#$%])[A-Za-z\\d,./!@#$%]{8,20}$")
	private String password;

	@ApiModelProperty(value = "새 비밀번호", required = true, example = "admin12@")
	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[,./!@#$%])[A-Za-z\\d,./!@#$%]{8,20}$")
	private String newPassword;

	@ApiModelProperty(value = "새 비밀번호 체크", example = "admin12@")
	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[,./!@#$%])[A-Za-z\\d,./!@#$%]{8,20}$")
	private String checkPassword;
}
