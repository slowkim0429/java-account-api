package com.virnect.account.adapter.inbound.dto.request.organization;

import java.time.ZonedDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.ContractStatus;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;

@Setter
@Getter
public class OrganizationContractRequestDto {
	@ApiModelProperty(value = "Organization Id", example = "1000000000", required = true)
	@NotNull(message = "Organization Id 은 반드시 입력되어야 합니다.")
	@Min(1000000000)
	private Long organizationId;

	@ApiModelProperty(value = "Item Id", example = "1000000000", required = true)
	@NotNull(message = "Item Id 은 반드시 입력되어야 합니다.")
	@Min(1000000000)
	private Long itemId;

	@ApiModelProperty(value = "Item Name", example = "Standard Item 1", required = true)
	@NotBlank(message = "Item Name 은 반드시 입력되어야 합니다.")
	private String itemName;

	@ApiModelProperty(value = "Item Type", example = "LICENSE", required = true)
	@NotNull(message = "Item Type 은 반드시 입력되어야 합니다.")
	private ItemType itemType;

	@ApiModelProperty(value = "Recurring Interval Type", example = "MONTH", required = true)
	@NotNull(message = "Recurring Interval Type 은 반드시 입력되어야 합니다.")
	private RecurringIntervalType recurringInterval;

	@ApiModelProperty(value = "Payment Type", example = "SUBSCRIPTION", required = true)
	@NotNull(message = "Payment Type 은 반드시 입력되어야 합니다.")
	private PaymentType paymentType;

	@ApiModelProperty(value = "Contract Id", example = "1000000000", required = true)
	@NotNull(message = "Contract Id 은 반드시 입력되어야 합니다.")
	@Min(1000000000)
	private Long contractId;

	@ApiModelProperty(value = "Coupon Id", example = "1000000000")
	@Min(1000000000)
	private Long couponId;

	@ApiModelProperty(value = "Contract Use Month (For Annually Subscription License Expired Calculation)", example = "0", required = false)
	private Long usedMonth;

	@ApiModelProperty(value = "Contract Status", example = "PROCESSING", required = true)
	@NotNull(message = "Contract Status 은 반드시 입력되어야 합니다.")
	private ContractStatus status;

	@ApiModelProperty(value = "Contract 시작일", example = "2023-08-11 05:44:13", required = true)
	@NotNull(message = "Contract 시작일 은 반드시 입력되어야 합니다.")
	private ZonedDateTime startDate;

	@ApiModelProperty(value = "Contract 종료일 (구독 결제라면 종료일이 갱신됨)", example = "2023-09-11 05:44:13", required = true)
	@NotNull(message = "Contract 종료일 은 반드시 입력되어야 합니다.")
	private ZonedDateTime endDate;

	@ApiModelProperty(hidden = true)
	public boolean isValid() {
		if (itemType.isAttribute() && status.isCanceled()) {
			return false;
		}
		return true;
	}

	@ApiModelProperty(hidden = true)
	public String getInvalidMessage() {
		return "부가서비스 유형의 아이템은 취소될 수 없습니다.";
	}
}
