package com.virnect.account.adapter.inbound.dto.request.license;

import org.apache.commons.lang.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"licenseAdditionalAttributeType"})
public class LicenseAdditionalAttributeRequestDto {
	@ApiModelProperty(value = "License Additional Attribute",
		example = "MAXIMUM_VIEW",
		required = true)
	private LicenseAdditionalAttributeType licenseAdditionalAttributeType;

	@ApiModelProperty(value = "Data Type", example = "NUMBER", required = true)
	private DataType dataType;

	@ApiModelProperty(value = "Data Value", example = "1000", required = true)
	private String dataValue;

	@ApiModelProperty(hidden = true)
	public boolean isValid() {
		if (licenseAdditionalAttributeType == null || dataType == null || StringUtils.isBlank(dataValue)) {
			return false;
		}
		return dataValue.length() <= 10;
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
		return licenseAdditionalAttributeType.getDataType().equals(dataType);
	}

	public LicenseAdditionalAttributeRequestDto(
		LicenseAdditionalAttributeType licenseAdditionalAttributeType,
		DataType dataType, String dataValue
	) {
		this.licenseAdditionalAttributeType = licenseAdditionalAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
	}
}
