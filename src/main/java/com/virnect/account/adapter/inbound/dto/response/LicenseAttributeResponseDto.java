package com.virnect.account.adapter.inbound.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.LicenseAttribute;

@ApiModel
@Getter
@NoArgsConstructor
public class LicenseAttributeResponseDto {
	@ApiModelProperty(value = "License Attribute id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "license attribute type", example = "MAXIMUM_GROUP")
	private LicenseAttributeType licenseAttributeType;

	@ApiModelProperty(value = "License Attribute data type", example = "NUMBER")
	private DataType dataType;

	@ApiModelProperty(value = "license attribute data value", example = "100")
	private String dataValue;

	@ApiModelProperty(value = "license attribute status", example = "USE")
	private UseStatus status;

	@QueryProjection
	public LicenseAttributeResponseDto(
		Long id,
		LicenseAttributeType licenseAttributeType,
		DataType dataType,
		String dataValue,
		UseStatus status
	) {
		this.id = id;
		this.licenseAttributeType = licenseAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
		this.status = status;
	}

	public LicenseAttributeResponseDto(LicenseAttribute licenseAttribute) {
		this.id = licenseAttribute.getId();
		this.licenseAttributeType = licenseAttribute.getAttributeType();
		this.dataType = licenseAttribute.getDataType();
		this.dataValue = licenseAttribute.getDataValue();
		this.status = licenseAttribute.getStatus();
	}
}
