package com.virnect.account.adapter.inbound.dto.request.organization;

import java.util.Locale;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.domain.enumclass.AcceptOrReject;

@Setter
@Getter
@NoArgsConstructor
public class OrganizationByAdminRequestDto {
	@ApiModelProperty(value = "Account Master User Id", example = "1000000001", required = true)
	@NotNull(message = "Account Master User Id는 반드시 입력되어야 합니다.")
	private Long masterUserId;

	@ApiModelProperty(value = "이메일", example = "test@test.com", required = true)
	@NotBlank(message = "이메일은 반드시 입력되어야 합니다.")
	@Email
	private String email;

	@ApiModelProperty(value = "Organization 명", example = "Virnect", required = true)
	@NotBlank(message = "Organization명 은 반드시 입력되어야 합니다.")
	@Size(max = 100)
	private String name;

	@ApiModelProperty(value = "전화번호 -[전화번호는 국가코드 포함 다음과 같은 형식이여야 합니다. +(국가코드)-('-'을 제외한 전화번호 최소 10자리)]",
					  required = true, example = "+82-0212457896")
	@NotBlank(message = "전화번호는 반드시 입력되어야 합니다.")
	@Size(max = 30)
	@Pattern(regexp = "^[0-9 +]*$")
	private String telNumber;

	@ApiModelProperty(value = "회사 명", example = "Virnect", required = true)
	@NotBlank(message = "회사명 반드시 입력되어야 합니다.")
	@Size(max = 30)
	private String company;

	@ApiModelProperty(value = "접속 지역", example = "ko_KR", required = true)
	@NotNull(message = "지역명은 반드시 입력되어야 합니다.")
	private Locale locale;

	@ApiModelProperty(value = "국가 (정보 수집용)", example = "ko_KR", required = true)
	@NotNull(message = "국가명은 반드시 입력되어야 합니다.")
	private Locale country;

	@ApiModelProperty(value = "주소", example = "10-15, Hangang-daero 7-gil, Yongsan-gu", required = true)
	@NotBlank(message = "주소는 반드시 입력되어야 합니다.")
	@Size(max = 100)
	private String address;

	@ApiModelProperty(value = "시/도", example = "Seoul")
	@Size(max = 30)
	private String city;

	@ApiModelProperty(value = "우편번호", example = "04379")
	@Size(max = 15)
	private String postalCode;

	@ApiModelProperty(value = "사업자등록번호", example = "1018216927")
	@NotBlank(message = "사업자등록번호는 반드시 입력되어야 합니다.")
	@Size(max = 30)
	private String businessRegistrationNumber;

	@ApiModelProperty(value = "광고 수신 동의 여부", required = true, example = "ACCEPT")
	@NotNull(message = "광고 수신 동의 여부 정보는 반드시 입력되어야 합니다.")
	private AcceptOrReject marketInfoReceive;
}
