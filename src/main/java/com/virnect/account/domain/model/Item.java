package com.virnect.account.domain.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.item.ItemRequestDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CurrencyType;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Getter
@Audited
@Table(name = "items",
	indexes = {
		@Index(name = "idx_name", columnList = "name")
		, @Index(name = "idx_item_type", columnList = "item_type")
		, @Index(name = "idx_license_id", columnList = "license_id")
		, @Index(name = "idx_payment_type", columnList = "payment_type")
		, @Index(name = "idx_recurring_interval", columnList = "recurring_interval")
		, @Index(name = "idx_status", columnList = "status")
		, @Index(name = "idx_use_status", columnList = "use_status")
		, @Index(name = "idx_hubspot_product_id", columnList = "hubspot_product_id")

	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "item_type", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private ItemType itemType;

	@Column(name = "license_id", nullable = false)
	private Long licenseId;

	@Column(name = "license_attribute_id")
	private Long licenseAttributeId;

	@Column(name = "payment_type", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	@Column(name = "recurring_interval", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private RecurringIntervalType recurringInterval;

	@Column(name = "currency_type", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private CurrencyType currencyType;

	@Column(name = "amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(name = "monthly_used_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal monthlyUsedAmount;

	@Column(name = "status", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private ApprovalStatus status = ApprovalStatus.REGISTER;

	@Column(name = "use_status", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private UseStatus useStatus = UseStatus.NONE;

	@Column(name = "hubspot_product_id")
	private Long hubSpotProductId;

	@Column(name = "is_exposed", nullable = false)
	private Boolean isExposed;

	private Item(
		String name, ItemType itemType, Long licenseId, Long licenseAttributeId, PaymentType paymentType,
		RecurringIntervalType recurringIntervalType, CurrencyType currencyType, BigDecimal amount,
		BigDecimal monthlyUsedAmount, ApprovalStatus status, UseStatus useStatus, Boolean isExposed
	) {
		this.name = name;
		this.itemType = itemType;
		this.licenseId = licenseId;
		this.licenseAttributeId = licenseAttributeId;
		this.paymentType = paymentType;
		this.recurringInterval = recurringIntervalType;
		this.currencyType = currencyType;
		this.amount = amount;
		this.monthlyUsedAmount = monthlyUsedAmount;
		this.status = status;
		this.useStatus = useStatus;
		this.isExposed = isExposed;
	}

	public static Item createLicenseItem(ItemRequestDto requestDto) {
		return new Item(
			requestDto.getName(),
			ItemType.LICENSE,
			requestDto.getLicenseId(),
			null,
			PaymentType.SUBSCRIPTION,
			RecurringIntervalType.valueOf(requestDto.getRecurringInterval()),
			CurrencyType.EUR,
			requestDto.getAmount(),
			requestDto.getMonthlyUsedAmount(),
			ApprovalStatus.REGISTER,
			UseStatus.NONE,
			false
		);
	}

	public static Item createAttributeItem(ItemRequestDto requestDto) {
		return new Item(
			requestDto.getName(),
			ItemType.ATTRIBUTE,
			requestDto.getLicenseId(),
			requestDto.getLicenseAttributeId(),
			PaymentType.NONRECURRING,
			RecurringIntervalType.valueOf(requestDto.getRecurringInterval()),
			CurrencyType.EUR,
			requestDto.getAmount(),
			new BigDecimal("0"),
			ApprovalStatus.REGISTER,
			UseStatus.NONE,
			false
		);
	}

	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}

	public void setUseStatus(UseStatus useStatus) {
		this.useStatus = useStatus;
	}

	public void setHubSpotProductId(Long hubSpotProductId) {
		this.hubSpotProductId = hubSpotProductId;
	}

	public void setIsExposed(Boolean isExposed) {
		this.isExposed = isExposed;
	}

	public void update(ItemRequestDto itemRequestDto, PaymentType paymentType) {
		this.name = itemRequestDto.getName();
		this.itemType = ItemType.valueOf(itemRequestDto.getItemType());
		this.licenseId = itemRequestDto.getLicenseId();
		this.licenseAttributeId = itemRequestDto.getLicenseAttributeId();
		this.paymentType = paymentType;
		this.recurringInterval = RecurringIntervalType.valueOf(itemRequestDto.getRecurringInterval());
		this.currencyType = CurrencyType.EUR;
		this.amount = itemRequestDto.getAmount();
		this.monthlyUsedAmount = itemRequestDto.getMonthlyUsedAmount();
	}

	public boolean isApproved() {
		return ApprovalStatus.APPROVED.equals(status);
	}

	public boolean isLicense() {
		return ItemType.LICENSE.equals(itemType);
	}
}
