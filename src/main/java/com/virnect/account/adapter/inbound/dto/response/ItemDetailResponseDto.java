package com.virnect.account.adapter.inbound.dto.response;

import java.math.BigDecimal;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CurrencyType;
import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.enumclass.UseStatus;

@Getter
@ApiModel
public class ItemDetailResponseDto {

	@ApiModelProperty(value = "item id", example = "1000000000")
	private final Long id;

	@ApiModelProperty(value = "아이템 명", example = "SQUARS PRO ITEM")
	private final String name;
	@ApiModelProperty(value = "승인 상태", example = "APPROVED")
	private final ApprovalStatus status;
	@ApiModelProperty(value = "노출 여부", example = "true")
	private final Boolean isExposed;
	@ApiModelProperty(value = "product id", example = "1000000000")
	private Long productId;
	@ApiModelProperty(value = "product type", example = "SQUARS")
	private ProductType productType;

	@ApiModelProperty(value = "product name", example = "Squars product")
	private String productName;
	@ApiModelProperty(value = "아이템 타입", example = "LICENSE")
	private ItemType itemType;
	@ApiModelProperty(value = "license id", example = "1000000000")
	private Long licenseId;
	@ApiModelProperty(value = "license Attribute id", example = "1000000000")
	private Long licenseAttributeId;
	@ApiModelProperty(value = "지불 타입", example = "SUBSCRIPTION")
	private PaymentType paymentType;
	@ApiModelProperty(value = "구독 기간 유형", example = "MONTH")
	private RecurringIntervalType recurringInterval;
	@ApiModelProperty(value = "통화 유형")
	private CurrencyType currencyType;
	@ApiModelProperty(value = "license grade id", example = "1000000000")
	private Long licenseGradeId;
	@ApiModelProperty(value = "license grade type", example = "PROFESSIONAL")
	private LicenseGradeType licenseGradeType;
	@ApiModelProperty(value = "license grade name", example = "Enterprise")
	private String licenseGradeName;
	@ApiModelProperty(value = "구매 금액", example = "100")
	private BigDecimal amount;
	@ApiModelProperty(value = "월간 사용 금액(환불 기준 가격)", example = "10")
	private BigDecimal monthlyUsedAmount;
	@ApiModelProperty(value = "부가서비스 유형 이름", example = "MAXIMUM_VIEW")
	private LicenseAdditionalAttributeType additionalAttributeType;
	@ApiModelProperty(value = "Data Type", example = "NUMBER")
	private DataType dataType;
	@ApiModelProperty(value = "Data Value", example = "1000")
	private String dataValue;
	@ApiModelProperty(value = "사용 상태", example = "NONE")
	private UseStatus useStatus;
	@ApiModelProperty(value = "허브스팟 상품 아이디", example = "900000000")
	private Long hubSpotProductId;
	@ApiModelProperty(value = "생성일", example = "2022-11-14T01:44:55")
	private String createdDate;

	@ApiModelProperty(value = "생성자 유저 아이디", example = "1000000000")
	private Long createdByUserId;

	@ApiModelProperty(value = "수정일", example = "2022-11-14T01:44:55")
	private String updatedDate;

	@ApiModelProperty(value = "수정자 유저 아이디", example = "1000000000")
	private Long updatedByUserId;

	@QueryProjection
	public ItemDetailResponseDto(
		Long id, String name, Long productId, ProductType productType, String productName, ItemType itemType,
		Long licenseId, Long licenseGradeId, LicenseGradeType licenseGradeType, String licenseGradeName,
		RecurringIntervalType recurringInterval, BigDecimal amount, BigDecimal monthlyUsedAmount,
		Long licenseAttributeId, ApprovalStatus status, Boolean isExposed, String createdDate, Long createdByUserId,
		String updatedDate, Long updatedByUserId, PaymentType paymentType, CurrencyType currencyType,
		UseStatus useStatus, Long hubSpotProductId
	) {
		this.id = id;
		this.name = name;
		this.productId = productId;
		this.productType = productType;
		this.productName = productName;
		this.itemType = itemType;
		this.licenseId = licenseId;
		this.licenseGradeId = licenseGradeId;
		this.licenseGradeType = licenseGradeType;
		this.licenseGradeName = licenseGradeName;
		this.recurringInterval = recurringInterval;
		this.amount = amount;
		this.monthlyUsedAmount = monthlyUsedAmount;
		this.licenseAttributeId = licenseAttributeId;
		this.status = status;
		this.isExposed = isExposed;
		this.createdDate = createdDate;
		this.createdByUserId = createdByUserId;
		this.updatedDate = updatedDate;
		this.updatedByUserId = updatedByUserId;
		this.paymentType = paymentType;
		this.currencyType = currencyType;
		this.useStatus = useStatus;
		this.hubSpotProductId = hubSpotProductId;
	}

	public void setAdditionalAttributeType(
		LicenseAdditionalAttributeType additionalAttributeType
	) {
		this.additionalAttributeType = additionalAttributeType;
	}

	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
}
