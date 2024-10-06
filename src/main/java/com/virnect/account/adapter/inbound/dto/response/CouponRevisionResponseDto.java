package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CouponBenefitOption;
import com.virnect.account.domain.enumclass.CouponLicenseGradeMatchingType;
import com.virnect.account.domain.enumclass.CouponRecurringIntervalMatchingType;
import com.virnect.account.domain.enumclass.CouponType;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Coupon;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class CouponRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "coupon id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "쿠폰 코드", example = "13TX0ASDKLJA4")
	private String code;

	@ApiModelProperty(value = "쿠폰명", example = "Pre-Release Event Coupon")
	private String name;

	@ApiModelProperty(value = "쿠폰에 대한 설명", example = "description")
	private String description;

	@ApiModelProperty(value = "쿠폰 유형", example = "PLUS_ATTRIBUTE")
	private CouponType couponType;

	@ApiModelProperty(value = "쿠폰을 통해 제공되는 옵션", example = "MAXIMUM_WORKSPACE")
	private CouponBenefitOption benefitOption;

	@ApiModelProperty(value = "쿠폰을 통해 추가로 제공되는 값", example = "3")
	private Long benefitValue;

	@ApiModelProperty(value = "쿠폰과 매칭되어야 하는 라이센스 등급", example = "PROFESSIONAL")
	private CouponLicenseGradeMatchingType couponLicenseGradeMatchingType;

	@ApiModelProperty(value = "쿠폰과 매칭되어야 하는 구독 주기", example = "MONTH")
	private CouponRecurringIntervalMatchingType couponRecurringIntervalMatchingType;

	@ApiModelProperty(value = "사용할 수 있는 최대 횟수", example = "800")
	private Long maxCount;

	@ApiModelProperty(value = "쿠폰 만료 기한", example = "2024-10-12 12:30:54")
	private String expiredDate;

	@ApiModelProperty(value = "쿠폰 승인 상태", example = "APPROVED")
	private ApprovalStatus status;

	@ApiModelProperty(value = "쿠폰 승인 상태", example = "UNUSE")
	private UseStatus useStatus;

	@ApiModelProperty(value = "등록 일자", example = "2022-10-12 12:30:54")
	private String createdDate;

	@ApiModelProperty(value = "등록한 사용자의 id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "수정 일시", example = "2022-10-12 12:30:54")
	private String updatedDate;

	@ApiModelProperty(value = "수정한 사용자의 id", example = "1000000000")
	private Long updatedBy;

	private CouponRevisionResponseDto(RevisionType revisionType, Coupon coupon) {
		this.revisionType = revisionType;
		this.id = coupon.getId();
		this.code = coupon.getCode();
		this.name = coupon.getName();
		this.description = coupon.getDescription();
		this.couponType = coupon.getCouponType();
		this.benefitOption = coupon.getBenefitOption();
		this.benefitValue = coupon.getBenefitValue();
		this.couponLicenseGradeMatchingType = coupon.getCouponLicenseGradeMatchingType();
		this.couponRecurringIntervalMatchingType = coupon.getCouponRecurringIntervalMatchingType();
		this.maxCount = coupon.getMaxCount();
		this.expiredDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(coupon.getExpiredDate());
		this.status = coupon.getStatus();
		this.useStatus = coupon.getUseStatus();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(coupon.getCreatedDate());
		this.createdBy = coupon.getCreatedBy();
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(coupon.getUpdatedDate());
		this.updatedBy = coupon.getLastModifiedBy();
	}

	public static CouponRevisionResponseDto of(Byte representation, Coupon coupon) {
		return new CouponRevisionResponseDto(RevisionType.valueOf(representation), coupon);
	}
}
