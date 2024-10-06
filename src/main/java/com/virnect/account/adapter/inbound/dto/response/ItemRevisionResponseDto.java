package com.virnect.account.adapter.inbound.dto.response;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CurrencyType;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Item;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class ItemRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "item id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "item name", example = "squars pro license")
	private String name;

	@ApiModelProperty(value = "item type", example = "ATTRIBUTE")
	private ItemType itemType;

	@ApiModelProperty(value = "item license id", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "item license attribute id", example = "1000000000")
	private Long licenseAttributeId;

	@ApiModelProperty(value = "item payment type", example = "SUBSCRIPTION")
	private PaymentType paymentType;

	@ApiModelProperty(value = "item exposed", example = "true")
	private Boolean isExposed;

	@ApiModelProperty(value = "item recurring interval type", example = "MONTH")
	private RecurringIntervalType recurringInterval;

	@ApiModelProperty(value = "item currency type", example = "EUR")
	private CurrencyType currencyType;

	@ApiModelProperty(value = "item amount", example = "10")
	private BigDecimal amount;

	@ApiModelProperty(value = "item monthly used amount", example = "10")
	private BigDecimal monthlyUsedAmount;

	@ApiModelProperty(value = "item status", example = "APPROVED")
	private ApprovalStatus status;

	@ApiModelProperty(value = "item use status", example = "USE")
	private UseStatus useStatus;

	@ApiModelProperty(value = "허브스팟 상품 아이디", example = "900000000")
	private Long hubSpotProductId;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03T11:15:30")
	private String updatedDate;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "등록일시", example = "2022-01-03T11:15:30")
	private String createdDate;

	public ItemRevisionResponseDto(RevisionType revisionType, Item item) {
		this.revisionType = revisionType;
		this.id = item.getId();
		this.name = item.getName();
		this.itemType = item.getItemType();
		this.licenseId = item.getLicenseId();
		this.licenseAttributeId = item.getLicenseAttributeId();
		this.paymentType = item.getPaymentType();
		this.recurringInterval = item.getRecurringInterval();
		this.currencyType = item.getCurrencyType();
		this.amount = item.getAmount();
		this.monthlyUsedAmount = item.getMonthlyUsedAmount();
		this.status = item.getStatus();
		this.isExposed = item.getIsExposed();
		this.useStatus = item.getUseStatus();
		this.hubSpotProductId = item.getHubSpotProductId();
		this.updatedBy = item.getLastModifiedBy();
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(item.getUpdatedDate());
		this.createdBy = item.getCreatedBy();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(item.getCreatedDate());
	}

	public static ItemRevisionResponseDto of(Byte representation, Item item) {
		return new ItemRevisionResponseDto(RevisionType.valueOf(representation), item);
	}
}
