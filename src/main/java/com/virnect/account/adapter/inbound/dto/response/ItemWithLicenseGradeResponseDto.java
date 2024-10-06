package com.virnect.account.adapter.inbound.dto.response;

import java.math.BigDecimal;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.CurrencyType;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;

@Getter
@ApiModel
public class ItemWithLicenseGradeResponseDto {

	@ApiModelProperty(value = "아이템 id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "아이템 명", example = "SQUARS PRO ITEM")
	private String name;

	@ApiModelProperty(value = "아이템 구독 기간 유형", example = "MONTH")
	private RecurringIntervalType recurringInterval;

	@ApiModelProperty(value = "아이템 구매 금액", example = "11000")
	private BigDecimal amount;

	@ApiModelProperty(value = "아이템 월별 금액", example = "1000")
	private BigDecimal monthlyUsedAmount;

	@ApiModelProperty(value = "아이템 currency type", example = "EUR")
	private CurrencyType currencyType;

	@ApiModelProperty(value = "아이템 타입", example = "LICENSE")
	private ItemType itemType;

	@ApiModelProperty(value = "아이템 결제 타입", example = "SUBSCRIPTION")
	private PaymentType paymentType;

	@ApiModelProperty(value = "License id", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "License description", example = "Recommended for starters and ...")
	private String licenseDescription;

	@ApiModelProperty(value = "sales target", example = "For Starter")
	private String salesTarget;

	@ApiModelProperty(value = "LicenseGrade 등급 타입", example = "FREE_PLUS")
	private LicenseGradeType licenseGradeType;

	@ApiModelProperty(value = "LicenseGrade 이름", example = "Free +")
	private String licenseGradeName;

	@ApiModelProperty(value = "LicenseGrade 설명", example = "This is Free + license grade")
	private String licenseGradeDescription;

	@QueryProjection
	public ItemWithLicenseGradeResponseDto(
		Long id, String name, RecurringIntervalType recurringInterval, BigDecimal amount, BigDecimal monthlyUsedAmount,
		CurrencyType currencyType, ItemType itemType, PaymentType paymentType, Long licenseId,
		String licenseDescription, String salesTarget, LicenseGradeType licenseGradeType, String licenseGradeName,
		String licenseGradeDescription
	) {
		this.id = id;
		this.name = name;
		this.recurringInterval = recurringInterval;
		this.amount = amount;
		this.monthlyUsedAmount = monthlyUsedAmount;
		this.currencyType = currencyType;
		this.itemType = itemType;
		this.paymentType = paymentType;
		this.licenseId = licenseId;
		this.licenseDescription = licenseDescription;
		this.salesTarget = salesTarget;
		this.licenseGradeType = licenseGradeType;
		this.licenseGradeName = licenseGradeName;
		this.licenseGradeDescription = licenseGradeDescription;
	}
}
