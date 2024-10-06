package com.virnect.account.adapter.inbound.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;

@Getter
@ApiModel
public class ItemAndLicenseGradeAndLicenseAttributesResponseDto {
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

	@ApiModelProperty(value = "월간 구독 가격", example = "1")
	private BigDecimal monthlyUsedAmount;

	@ApiModelProperty(value = "라이선스 등급 타입", example = "ENTERPRISE")
	private LicenseGradeType licenseGradeType;

	@ApiModelProperty(value = "라이선스 등급 이름", example = "Enterprise")
	private String licenseGradeName;

	@ApiModelProperty(value = "sales target", example = "For starter")
	private String salesTarget;

	@ApiModelProperty(value = "구독 item 속성 값")
	private List<LicenseAttributeResponseDto> licenseAttributes;

	@ApiModelProperty(value = "부가 서비스 item 속성 값")
	private List<LicenseAdditionalAttributeResponseDto> licenseAdditionalAttributes;

	@ApiModelProperty(value = "부가 서비스 item list")
	private List<AdditionalItemAndLicenseAttributeResponseDto> additionalItems;

	@QueryProjection
	public ItemAndLicenseGradeAndLicenseAttributesResponseDto(
		Long id, String name, RecurringIntervalType recurringInterval, BigDecimal amount, ItemType itemType,
		LicenseGradeType licenseGradeType, String licenseGradeName
	) {
		this.id = id;
		this.name = name;
		this.recurringInterval = recurringInterval;
		this.amount = amount;
		this.itemType = itemType;
		this.licenseGradeType = licenseGradeType;
		this.licenseGradeName = licenseGradeName;
	}

	public ItemAndLicenseGradeAndLicenseAttributesResponseDto(
		ItemAndLicenseGradeResponseDto itemAndLicenseGradeResponseDto
	) {
		this.id = itemAndLicenseGradeResponseDto.getId();
		this.name = itemAndLicenseGradeResponseDto.getName();
		this.recurringInterval = itemAndLicenseGradeResponseDto.getRecurringInterval();
		this.amount = itemAndLicenseGradeResponseDto.getAmount();
		this.annuallyAmount = itemAndLicenseGradeResponseDto.getAnnuallyAmount();
		this.discountAmount = itemAndLicenseGradeResponseDto.getDiscountAmount();
		this.itemType = itemAndLicenseGradeResponseDto.getItemType();
		this.monthlyUsedAmount = itemAndLicenseGradeResponseDto.getMonthlyUsedAmount();
		this.licenseGradeType = itemAndLicenseGradeResponseDto.getLicenseGradeType();
		this.licenseGradeName = itemAndLicenseGradeResponseDto.getLicenseGradeName();
		this.salesTarget = itemAndLicenseGradeResponseDto.getSalesTarget();
	}

	public void setLicenseAttributes(
		List<LicenseAttributeResponseDto> licenseAttributes
	) {
		this.licenseAttributes = licenseAttributes;
	}

	public void setLicenseAdditionalAttributes(
		List<LicenseAdditionalAttributeResponseDto> licenseAdditionalAttributes
	) {
		this.licenseAdditionalAttributes = licenseAdditionalAttributes;
	}

	public void setAdditionalItems(
		List<AdditionalItemAndLicenseAttributeResponseDto> additionalItems
	) {
		this.additionalItems = additionalItems;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public void setAnnuallyAmount(BigDecimal annuallyAmount) {
		this.annuallyAmount = annuallyAmount;
	}
}
