package com.virnect.account.adapter.inbound.dto.response;

import java.math.BigDecimal;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;

@Getter
public class ItemAndLicenseGradeResponseDto {
	@ApiModelProperty(value = "아이템 아이디", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "아이템 이름", example = "SQUARS PRO ITEM")
	private String name;

	@ApiModelProperty(value = "구독 기간 유형", example = "MONTH")
	private RecurringIntervalType recurringInterval;

	@ApiModelProperty(value = "구매 금액", example = "11000")
	private BigDecimal amount;

	@ApiModelProperty(value = "연간 금액", example = "1000")
	private BigDecimal annuallyAmount;

	@ApiModelProperty(value = "할인 금액", example = "1000")
	private BigDecimal discountAmount;

	@ApiModelProperty(value = "아이템 타입", example = "LICENSE")
	private ItemType itemType;

	@ApiModelProperty(value = "라이선스 아이디", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "월간 구독 가격", example = "1")
	private BigDecimal monthlyUsedAmount;

	@ApiModelProperty(value = "라이선스 등급 타입", example = "ENTERPRISE")
	private LicenseGradeType licenseGradeType;

	@ApiModelProperty(value = "라이선스 등급 이름", example = "Enterprise")
	private String licenseGradeName;

	@ApiModelProperty(value = "sales target", example = "For starter")
	private String salesTarget;

	@QueryProjection
	public ItemAndLicenseGradeResponseDto(
		Long id, String name, RecurringIntervalType recurringInterval, BigDecimal amount, ItemType itemType,
		Long licenseId, BigDecimal monthlyUsedAmount, LicenseGradeType licenseGradeType, String licenseGradeName,
		String salesTarget
	) {
		this.id = id;
		this.name = name;
		this.recurringInterval = recurringInterval;
		this.amount = amount;
		this.itemType = itemType;
		this.licenseId = licenseId;
		this.monthlyUsedAmount = monthlyUsedAmount;
		this.licenseGradeType = licenseGradeType;
		this.licenseGradeName = licenseGradeName;
		this.salesTarget = salesTarget;
	}

	public void setAnnuallyAmount(BigDecimal annuallyAmount) {
		this.annuallyAmount = annuallyAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}
}
