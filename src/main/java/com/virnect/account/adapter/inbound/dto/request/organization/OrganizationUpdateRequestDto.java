package com.virnect.account.adapter.inbound.dto.request.organization;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.adapter.inbound.dto.request.validate.NotEmoji;
import com.virnect.account.domain.enumclass.CustomerType;

@Setter
@Getter
@NoArgsConstructor
public class OrganizationUpdateRequestDto {
	@ApiModelProperty(value = "Locale Id", example = "1000000001", required = true)
	@NotNull(message = "Locale Id 은 반드시 입력되어야 합니다.")
	private Long localeId;

	@ApiModelProperty(value = "Organization Email", example = "virnect@virnect.com", required = true)
	@NotBlank(message = "Organization Email 은 반드시 입력되어야 합니다.")
	@Size(max = 100)
	@Email
	private String email;

	@ApiModelProperty(value = "Organization 명", example = "virnect", required = true)
	@NotBlank(message = "Organization 명은 반드시 입력되어야 합니다.")
	@NotEmoji
	@Size(max = 50)
	private String name;

	@ApiModelProperty(value = "주소", example = "10-15, Hangang-daero 7-gil, Yongsan-gu", required = true)
	@NotBlank(message = "address는 반드시 입력되어야 합니다.")
	@NotEmoji
	@Size(max = 100)
	private String address;

	@ApiModelProperty(value = "시/도", example = "Seoul", required = true)
	@NotBlank(message = "city는 반드시 입력되어야 합니다.")
	@NotEmoji
	@Size(max = 50)
	private String city;

	@ApiModelProperty(value = "State Code", example = "CA-BC")
	private String stateCode;

	@ApiModelProperty(value = "구", example = "Yongsan-gu")
	@NotEmoji
	@Size(max = 50)
	private String province;

	@ApiModelProperty(value = "우편번호", example = "04379")
	@NotEmoji
	@Size(max = 20)
	private String postalCode;

	@ApiModelProperty(value = "사업자등록번호", example = "123456789")
	@Pattern(regexp = "^[\\d\\p{L}-\\s]*$")
	@Size(max = 30)
	private String vatIdentificationNumber;

	@ApiModelProperty(value = "구매자 유형", example = "[NATURAL_PERSON, LEGAL_PERSON]", required = true)
	@NotBlank(message = "customerType은 반드시 입력되어야 합니다.")
	@CommonEnum(enumClass = CustomerType.class)
	private String customerType;

	@ApiModelProperty(value = "first name", example = "Justin", required = true)
	@NotBlank(message = "firstName은 반드시 입력되어야 합니다.")
	@NotEmoji
	@Size(max = 50)
	private String firstName;

	@ApiModelProperty(value = "last name", example = "Bieber", required = true)
	@NotBlank(message = "lastName은 반드시 입력되어야 합니다.")
	@NotEmoji
	@Size(max = 50)
	private String lastName;

	@ApiModelProperty(value = "phone number", example = "Yongsan-gu", required = true)
	@NotBlank(message = "phoneNumber은 반드시 입력되어야 합니다.")
	@Pattern(regexp = "^[1-9]\\d*(-\\d+)*$")
	@Size(max = 50)
	private String phoneNumber;

	@ApiModelProperty(value = "corporate number", example = "2345423")
	@Pattern(regexp = "^[\\d\\p{L}-\\s]*$")
	@Size(max = 30)
	private String corporateNumber;

	@ApiModelProperty(hidden = true)
	public CustomerType valueOfCustomerType() {
		return CustomerType.valueOf(customerType);
	}

	@ApiModelProperty(hidden = true)
	public boolean isValid() {
		if (CustomerType.LEGAL_PERSON.equals(valueOfCustomerType()) &&
			(StringUtils.isBlank(corporateNumber) || StringUtils.isBlank(vatIdentificationNumber))) {
			return false;
		}

		if (CustomerType.NATURAL_PERSON.equals(valueOfCustomerType()) && StringUtils.isNotBlank(corporateNumber)) {
			return false;
		}

		return true;
	}

	@ApiModelProperty(hidden = true)
	public String getInvalidMessage() {
		return "customer type 이 LEGAL_PERSON 일 경우에만 corporate number 가 필수 입력 값입니다.";
	}
}
