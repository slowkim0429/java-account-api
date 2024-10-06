package com.virnect.account.adapter.inbound.dto.request.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.RecurringIntervalType;

@Setter
@ApiModel
public class ItemExposureSearchDto {

	@ApiModelProperty(value = "구독 기간 유형", example = "YEAR")
	@CommonEnum(enumClass = RecurringIntervalType.class)
	private String recurringInterval;

	public RecurringIntervalType getRecurringInterval() {
		return recurringInterval == null ? null : RecurringIntervalType.valueOf(recurringInterval);
	}
}
