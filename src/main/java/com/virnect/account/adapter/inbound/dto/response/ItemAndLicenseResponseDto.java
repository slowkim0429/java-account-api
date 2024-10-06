package com.virnect.account.adapter.inbound.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
public class ItemAndLicenseResponseDto {
	@ApiModelProperty(value = "item id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "license id", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "상품명", example = "squars 상품")
	private String name;

	@ApiModelProperty(value = "아이템 유형", example = "LICENSE")
	private ItemType itemType;

	@ApiModelProperty(value = "라이선스 결제 타입", example = "SUBSCRIPTION")
	private PaymentType paymentType;

	@ApiModelProperty(value = "라이선스 결제 간격", example = "MONTH")
	private RecurringIntervalType recurringInterval;

	@ApiModelProperty(value = "product id", example = "1000000000")
	private Long productId;

	@ApiModelProperty(value = "등급 타입", example = "SQUARS")
	private ProductType productType;

	@ApiModelProperty(value = "등급 이름", example = "squars product")
	private String productName;

	@ApiModelProperty(value = "라이선스 유형 id", example = "1000000000")
	private Long licenseGradeId;

	@ApiModelProperty(value = "등급 이름", example = "Enterprise")
	private String licenseGradeName;

	@ApiModelProperty(value = "등급 타입", example = "ENTERPRISE")
	private LicenseGradeType licenseGradeType;

	@ApiModelProperty(value = "상품 가격(EUR)", example = "100")
	private BigDecimal amount;

	@ApiModelProperty(value = "상품 승인 상태", example = "APPROVED")
	private ApprovalStatus status;

	@ApiModelProperty(value = "상품 사용 유무", example = "USE")
	private UseStatus useStatus;

	@ApiModelProperty(value = "노출 여부", example = "true")
	private Boolean isExposed;

	@ApiModelProperty(value = "등록 일자", example = "2022-01-03 10:15:30")
	private String createdDate;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "수정 일시", example = "2022-01-03 10:15:30")
	private String updatedDate;

	@ApiModelProperty(value = "수정자", example = "1000000000")
	private Long updatedBy;

	@QueryProjection
	public ItemAndLicenseResponseDto(
		Long id, Long licenseId, String name, ItemType itemType, PaymentType paymentType,
		RecurringIntervalType recurringInterval, Long productId, ProductType productType, String productName,
		Long licenseGradeId, String licenseGradeName, LicenseGradeType licenseGradeType, BigDecimal amount,
		UseStatus useStatus, ApprovalStatus status, Boolean isExposed, ZonedDateTime createdDate, Long createdBy,
		ZonedDateTime updatedDate, Long updatedBy
	) {
		this.id = id;
		this.licenseId = licenseId;
		this.name = name;
		this.itemType = itemType;
		this.paymentType = paymentType;
		this.recurringInterval = recurringInterval;
		this.productId = productId;
		this.productType = productType;
		this.productName = productName;
		this.licenseGradeId = licenseGradeId;
		this.licenseGradeName = licenseGradeName;
		this.licenseGradeType = licenseGradeType;
		this.amount = amount;
		this.useStatus = useStatus;
		this.status = status;
		this.isExposed = isExposed;
		this.createdBy = createdBy;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedBy = updatedBy;
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
	}
}
