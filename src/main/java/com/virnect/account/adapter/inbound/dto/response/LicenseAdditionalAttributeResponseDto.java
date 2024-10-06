package com.virnect.account.adapter.inbound.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.LicenseAttribute;

@ApiModel
@Getter
@NoArgsConstructor
public class LicenseAdditionalAttributeResponseDto {
	@ApiModelProperty(value = "License Attribute id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "license additional attribute type", example = "MAXIMUM_VIEW")
	private LicenseAdditionalAttributeType licenseAdditionalAttributeType;

	@ApiModelProperty(value = "License additional attribute data type", example = "NUMBER")
	private DataType dataType;

	@ApiModelProperty(value = "license additional attribute data value", example = "100")
	private String dataValue;

	@ApiModelProperty(value = "license additional attribute status", example = "USE")
	private UseStatus status;

	@QueryProjection
	public LicenseAdditionalAttributeResponseDto(
		Long id,
		LicenseAdditionalAttributeType licenseAdditionalAttributeType,
		DataType dataType,
		String dataValue,
		UseStatus status
	) {
		this.id = id;
		this.licenseAdditionalAttributeType = licenseAdditionalAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
		this.status = status;
	}

	public LicenseAdditionalAttributeResponseDto(LicenseAttribute licenseAttribute) {
		this.id = licenseAttribute.getId();
		this.licenseAdditionalAttributeType = licenseAttribute.getAdditionalAttributeType();
		this.dataType = licenseAttribute.getDataType();
		this.dataValue = licenseAttribute.getDataValue();
		this.status = licenseAttribute.getStatus();
	}
}
