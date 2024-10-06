package com.virnect.account.adapter.outbound.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CurrencyType;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.License;
import com.virnect.account.domain.model.LicenseGrade;

@Getter
@NoArgsConstructor
public class ItemSendDto {
	private Long id;
	private String name;
	private ItemType itemType;
	private Long licenseId;
	private String licenseName;
	private String licenseSalesTarget;
	private ApprovalStatus licenseStatus;
	private UseStatus licenseUseStatus;
	private PaymentType paymentType;
	private RecurringIntervalType recurringInterval;
	private CurrencyType currencyType;
	private BigDecimal amount;
	private BigDecimal monthlyUsedAmount;
	private ApprovalStatus status;
	private UseStatus useStatus;
	private Long hubspotProductId;
	private String licenseGradeName;
	private LicenseGradeType licenseGradeType;
	private Boolean isExposed;
	private Long createdBy;
	private Long lastModifiedBy;

	private ItemSendDto(
		Item item, License license, LicenseGrade licenseGrade
	) {
		this.id = item.getId();
		this.name = item.getName();
		this.itemType = item.getItemType();
		this.licenseId = license.getId();
		this.licenseName = license.getName();
		this.licenseSalesTarget = license.getSalesTarget();
		this.licenseStatus = license.getStatus();
		this.licenseUseStatus = license.getUseStatus();
		this.paymentType = item.getPaymentType();
		this.recurringInterval = item.getRecurringInterval();
		this.currencyType = item.getCurrencyType();
		this.amount = item.getAmount();
		this.monthlyUsedAmount = item.getMonthlyUsedAmount();
		this.status = item.getStatus();
		this.useStatus = item.getUseStatus();
		this.hubspotProductId = item.getHubSpotProductId();
		this.licenseGradeName = licenseGrade.getName();
		this.licenseGradeType = licenseGrade.getGradeType();
		this.isExposed = item.getIsExposed();
		this.createdBy = item.getCreatedBy();
		this.lastModifiedBy = item.getLastModifiedBy();
	}

	public static ItemSendDto of(Item item, License license, LicenseGrade licenseGrade) {
		return new ItemSendDto(item, license, licenseGrade);
	}
}
