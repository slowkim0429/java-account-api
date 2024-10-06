package com.virnect.account.adapter.inbound.dto.response;

import java.math.BigDecimal;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;

@ApiModel
@Getter
@NoArgsConstructor
public class AdditionalItemAndLicenseAttributeResponseDto {
	@ApiModelProperty(value = "Additional item id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Additional item name", example = "SQUARS STANDARD ITEM")
	private String name;

	@ApiModelProperty(value = "구독 기간 유형", example = "NONE")
	private RecurringIntervalType recurringInterval;

	@ApiModelProperty(value = "금액", example = "11000")
	private BigDecimal amount;

	@ApiModelProperty(value = "아이템 타입", example = "ATTRIBUTE")
	private ItemType itemType;

	@ApiModelProperty(value = "license additional attribute type", example = "MAXIMUM_VIEW")
	private LicenseAdditionalAttributeType licenseAdditionalAttributeType;

	@ApiModelProperty(value = "License additional attribute data type", example = "NUMBER")
	private DataType dataType;

	@ApiModelProperty(value = "license additional attribute data value", example = "100")
	private String dataValue;

	@QueryProjection
	public AdditionalItemAndLicenseAttributeResponseDto(
		Long id, String name, RecurringIntervalType recurringInterval, BigDecimal amount, ItemType itemType,
		LicenseAdditionalAttributeType licenseAdditionalAttributeType, DataType dataType, String dataValue
	) {
		this.id = id;
		this.name = name;
		this.recurringInterval = recurringInterval;
		this.amount = amount;
		this.itemType = itemType;
		this.licenseAdditionalAttributeType = licenseAdditionalAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
	}
}
