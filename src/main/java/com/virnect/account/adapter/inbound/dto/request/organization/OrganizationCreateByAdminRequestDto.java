package com.virnect.account.adapter.inbound.dto.request.organization;

import java.util.Locale;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.PasswordPolicy;

@Setter
@Getter
@ApiModel
public class OrganizationCreateByAdminRequestDto {
	@ApiModelProperty(value = "이메일", example = "test@test.com", required = true)
	@NotBlank(message = "이메일은 반드시 입력되어야 합니다.")
	@Email
	private String email;

	@ApiModelProperty(value = "로그인 비밀번호", required = true, example = "!2dfksdklWa")
	@NotBlank(message = "로그인 패스워드는 반드시 입력되어야 합니다.")
	@PasswordPolicy(minLength = 8, maxLength = 20, maxNumberOfRepeatedLetter = 3)
	private String password;

	@ApiModelProperty(value = "사용 지역 아이디", required = true, example = "1000000003")
	@NotNull(message = "사용 지역은 반드시 입력되어야 합니다.")
	@Min(1000000000)
	private Long localeId;

	@ApiModelProperty(value = "사용 지역 아이디", required = true, example = "KR")
	@NotNull(message = "사용 지역은 코드는 반드시 입력되어야 합니다.")
	private String localeCode;

	@ApiModelProperty(value = "사용 언어 명", required = true, example = "ko_KR")
	@NotNull(message = "사용 언어는 반드시 입력되어야 합니다.")
	private Locale language;

	@ApiModelProperty(value = "이메일", example = "test@test.com", required = true)
	@NotBlank(message = "이메일은 반드시 입력되어야 합니다.")
	@Email
	private String organizationEmail;

	@ApiModelProperty(value = "Organization 명", example = "Virnect Account", required = true)
	@NotBlank(message = "Organization명 은 반드시 입력되어야 합니다.")
	@Size(max = 20)
	private String name;

	@ApiModelProperty(value = "주소", example = "10-15, Hangang-daero 7-gil, Yongsan-gu", required = true)
	@NotBlank(message = "주소는 반드시 입력되어야 합니다.")
	@Size(max = 50)
	private String address;

	@ApiModelProperty(value = "도시", example = "Seoul")
	@Size(max = 50)
	private String city;

	@ApiModelProperty(value = "시/도", example = "gyeonggi-do")
	@Size(max = 50)
	private String province;

	@ApiModelProperty(value = "우편번호", example = "04379")
	@Size(max = 20)
	private String postalCode;

	@ApiModelProperty(value = "사업자등록번호", example = "1018216927")
	@NotBlank(message = "사업자등록번호는 반드시 입력되어야 합니다.")
	@Size(max = 50)
	private String vatIdentificationNumber;
}
