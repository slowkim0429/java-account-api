package com.virnect.account.adapter.inbound.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.CurrencyType;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.model.LicenseAttribute;

@Getter
@ApiModel
public class ItemAndGradeWithLicenseAttributesResponseDto {

	@ApiModelProperty(value = "아이템 id", example = "1000000000")
	private Long id;

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

	@ApiModelProperty(value = "라이센스 설명", example = "Recommended for starters and ...")
	private String licenseDescription;

	@ApiModelProperty(value = "sales target", example = "For starter")
	private String salesTarget;

	@ApiModelProperty(value = "LicenseGrade 등급 타입", example = "FREE_PLUS")
	private LicenseGradeType licenseGradeType;

	@ApiModelProperty(value = "LicenseGrade 이름", example = "Free +")
	private String licenseGradeName;

	@ApiModelProperty(value = "연간 금액", example = "1000")
	private BigDecimal annuallyAmount;

	@ApiModelProperty(value = "할인 금액", example = "1000")
	private BigDecimal discountAmount;

	@ApiModelProperty(value = "LicenseGrade 설명", example = "This is Free license")
	private String licenseGradeDescription;

	@ApiModelProperty(value = "구독 item 속성 값")
	private List<LicenseAttributeResponseDto> licenseAttributes;

	@ApiModelProperty(value = "부가 item 속성 값")
	private List<AdditionalItemAndLicenseAttributeResponseDto> additionalLicenseAttributes;

	public ItemAndGradeWithLicenseAttributesResponseDto(
		ItemWithLicenseGradeResponseDto itemWithLicenseGrade
	) {
		this.id = itemWithLicenseGrade.getId();
		this.recurringInterval = itemWithLicenseGrade.getRecurringInterval();
		this.amount = itemWithLicenseGrade.getAmount();
		this.monthlyUsedAmount = itemWithLicenseGrade.getMonthlyUsedAmount();
		this.currencyType = itemWithLicenseGrade.getCurrencyType();
		this.itemType = itemWithLicenseGrade.getItemType();
		this.paymentType = itemWithLicenseGrade.getPaymentType();
		this.licenseGradeType = itemWithLicenseGrade.getLicenseGradeType();
		this.licenseGradeName = itemWithLicenseGrade.getLicenseGradeName();
		this.licenseDescription = itemWithLicenseGrade.getLicenseDescription();
		this.salesTarget = itemWithLicenseGrade.getSalesTarget();
		this.licenseGradeDescription = itemWithLicenseGrade.getLicenseGradeDescription();
	}

	public void setLicenseAttributes(
		List<LicenseAttribute> licenseAttributes
	) {
		this.licenseAttributes = licenseAttributes.stream()
			.map(LicenseAttributeResponseDto::new)
			.collect(Collectors.toList());
	}

	public void setAdditionalLicenseAttributes(
		List<AdditionalItemAndLicenseAttributeResponseDto> additionalLicenseAttributes
	) {
		this.additionalLicenseAttributes = additionalLicenseAttributes;
	}

	public void setAnnuallyAmount(BigDecimal annuallyAmount) {
		this.annuallyAmount = annuallyAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}
}
