package com.virnect.account.adapter.inbound.dto.request.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminUserRequestDto {
	@ApiModelProperty(value = "이메일", required = true, example = "test@virnect.com")
	@NotBlank(message = "이메일은 반드시 입력되어야 합니다.")
	@Email
	private String email;

	@ApiModelProperty(value = "비밀번호", required = true, example = "!2dfksdklWa")
	@NotBlank(message = "비밀번호는 반드시 입력되어야 합니다.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[,./!@#$%])[A-Za-z\\d,./!@#$%]{8,20}$")
	private String password;

	@ApiModelProperty(value = "사용 지역 아이디", required = true, example = "1000000003")
	@NotNull(message = "사용 지역은 반드시 입력되어야 합니다.")
	@Min(1000000000)
	private Long localeId;

	private AdminUserRequestDto(String email, String password, Long localeId) {
		this.email = email;
		this.password = password;
		this.localeId = localeId;
	}

	public static AdminUserRequestDto of(String email, String password, Long localeId) {
		return new AdminUserRequestDto(email, password, localeId);
	}

	public String getLowerCaseEmail() {
		return email.toLowerCase();
	}
}
