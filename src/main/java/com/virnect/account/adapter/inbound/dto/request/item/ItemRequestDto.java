package com.virnect.account.adapter.inbound.dto.request.item;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.validate.ItemTypeSubset;
import com.virnect.account.adapter.inbound.dto.request.validate.RecurringIntervalTypeSubset;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;

@Getter
@ApiModel
@NoArgsConstructor
public class ItemRequestDto {
	@Size(min = 1, max = 50, message = "상품명은 1~50자 까지만 가능합니다.")
	@NotBlank(message = "상품명은 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "상품명", example = "squars 상품", required = true)
	private String name;

	@ItemTypeSubset(anyOf = {ItemType.LICENSE, ItemType.ATTRIBUTE})
	@NotBlank(message = "아이템 타입은 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "아이템 타입", example = "LICENSE", required = true)
	private String itemType;

	@Min(value = 1000000000, message = "license id 값은 1000000000보다 같거나 커야 합니다.")
	@NotNull(message = "license id는 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "license id", example = "1000000000", required = true)
	private Long licenseId;

	@Min(value = 1000000000, message = "license attribute id 값은 1000000000보다 같거나 커야 합니다.")
	@ApiModelProperty(value = "license attribute id", example = "1000000000")
	private Long licenseAttributeId;

	@NotBlank(message = "recurringInterval은 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "구독 기간 유형", example = "MONTH", required = true)
	@RecurringIntervalTypeSubset(anyOf = {RecurringIntervalType.NONE, RecurringIntervalType.MONTH,
		RecurringIntervalType.YEAR})
	private String recurringInterval;

	@DecimalMin(value = "0")
	@Digits(integer = 10, fraction = 0)
	@NotNull(message = "구매 금액은 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "구매 금액", example = "100", required = true)
	private BigDecimal amount;

	@DecimalMin(value = "0")
	@Digits(integer = 10, fraction = 0)
	@NotNull(message = "monthlyUsedAmount는 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "월간 사용 금액(환불 기준 가격)", example = "10", required = true)
	private BigDecimal monthlyUsedAmount;

	private ItemRequestDto(
		String name,
		String itemType,
		Long licenseId,
		Long licenseAttributeId,
		String recurringInterval,
		BigDecimal amount,
		BigDecimal monthlyUsedAmount
	) {
		this.name = name;
		this.itemType = itemType;
		this.licenseId = licenseId;
		this.licenseAttributeId = licenseAttributeId;
		this.recurringInterval = recurringInterval;
		this.amount = amount;
		this.monthlyUsedAmount = monthlyUsedAmount;
	}

	public static ItemRequestDto of(
		String name,
		String itemType,
		Long licenseId,
		Long licenseAttributeId,
		String recurringInterval,
		BigDecimal amount,
		BigDecimal monthlyUsedAmount
	) {
		return new ItemRequestDto(
			name,
			itemType,
			licenseId,
			licenseAttributeId,
			recurringInterval,
			amount,
			monthlyUsedAmount
		);
	}

	@ApiModelProperty(hidden = true)
	public RecurringIntervalType recurringIntervalTypeValueOf() {
		return RecurringIntervalType.valueOf(this.recurringInterval);
	}

	@ApiModelProperty(hidden = true)
	public ItemType itemTypeValueOf() {
		return ItemType.valueOf(this.itemType);
	}
}
