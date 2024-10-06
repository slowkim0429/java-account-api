package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ContractStatus;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@NoArgsConstructor
public class OrganizationContractResponseDto {
	@ApiModelProperty(value = "Organization Contract Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Organization Id", example = "1000000000")
	private Long organizationId;

	@ApiModelProperty(value = "Contract Id", example = "1000000000")
	private Long contractId;

	@ApiModelProperty(value = "Item Id", example = "1000000000")
	private Long itemId;

	@ApiModelProperty(value = "Item Name", example = "Standard Item 1")
	private String itemName;

	@ApiModelProperty(value = "Item Type", example = "LICENSE")
	private ItemType itemType;

	@ApiModelProperty(value = "Recurring Interval Type", example = "MONTH")
	private RecurringIntervalType recurringInterval;

	@ApiModelProperty(value = "Payment Type", example = "SUBSCRIPTION")
	private PaymentType paymentType;

	@ApiModelProperty(value = "Contract Status", example = "PROCESSING")
	private ContractStatus status;

	@ApiModelProperty(value = "Contract Start Date", example = "2022-04-26 11:29:33")
	private String startDate;

	@ApiModelProperty(value = "Contract End Date", example = "2022-04-26 11:29:33")
	private String endDate;

	@ApiModelProperty(value = "Organization Contract Created Date", example = "2022-04-26 11:29:33")
	private String createdDate;

	@ApiModelProperty(value = "Organization Contract Updated Date", example = "2022-04-26 11:29:33")
	private String updatedDate;

	@ApiModelProperty(value = "Contract Create User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "Contract Update User Id", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "Coupon Id", example = "1000000000")
	private Long couponId;

	@QueryProjection
	public OrganizationContractResponseDto(
		Long id, Long organizationId, Long contractId, Long itemId, String itemName, ItemType itemType,
		RecurringIntervalType recurringInterval, PaymentType paymentType, ContractStatus status,
		ZonedDateTime startDate, ZonedDateTime endDate, ZonedDateTime createdDate, ZonedDateTime updatedDate,
		Long createdBy, Long updatedBy, Long couponId
	) {
		this.id = id;
		this.organizationId = organizationId;
		this.contractId = contractId;
		this.itemId = itemId;
		this.itemName = itemName;
		this.itemType = itemType;
		this.recurringInterval = recurringInterval;
		this.paymentType = paymentType;
		this.status = status;
		this.startDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(startDate);
		this.endDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(endDate);
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.couponId = couponId;
	}
}
