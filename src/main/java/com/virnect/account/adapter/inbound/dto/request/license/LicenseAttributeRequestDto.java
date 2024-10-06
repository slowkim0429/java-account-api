package com.virnect.account.adapter.inbound.dto.request.license;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"licenseAttributeType"})
public class LicenseAttributeRequestDto {
	@ApiModelProperty(value = "License Attribute",
		example = "MAXIMUM_WORKSPACE",
		required = true)
	@NotNull(message = "라이선스 속성 타입은 반드시 입력되어야 합니다.")
	private LicenseAttributeType licenseAttributeType;

	@ApiModelProperty(value = "Data Type", example = "NUMBER", required = true)
	@NotNull(message = "라이선스 속성의 data type은 반드시 입력되어야 합니다.")
	private DataType dataType;

	@ApiModelProperty(value = "Data Value", example = "1000", required = true)
	@NotBlank(message = "data value는 반드시 입력되어야 합니다.")
	@Size(min = 1, max = 10)
	private String dataValue;

	public LicenseAttributeRequestDto(
		LicenseAttributeType licenseAttributeType,
		DataType dataType, String dataValue
	) {
		this.licenseAttributeType = licenseAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
	}

	@ApiModelProperty(hidden = true)
	public boolean isDataValueValid() {
		if (DataType.BOOL.equals(dataType)) {
			return dataValue.equalsIgnoreCase("true") || dataValue.equalsIgnoreCase("false");
		}

		if (DataType.NUMBER.equals(dataType)) {
			try {
				Long.parseLong(dataValue);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	@ApiModelProperty(hidden = true)
	public boolean isDataTypeValid() {
		if (licenseAttributeType == null) {
			return false;
		}
		return licenseAttributeType.getDataType().equals(dataType);
	}
}
