package com.virnect.account.adapter.outbound.request;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CouponBenefitOption;
import com.virnect.account.domain.enumclass.CouponLicenseGradeMatchingType;
import com.virnect.account.domain.enumclass.CouponRecurringIntervalMatchingType;
import com.virnect.account.domain.enumclass.CouponType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Coupon;

@Getter
@NoArgsConstructor
public class CouponSendDto {
	private Long id;
	private String code;
	private String name;
	private String description;
	private CouponType couponType;
	private Long maxCount;
	private ZonedDateTime expiredDate;
	private ApprovalStatus status;
	private UseStatus useStatus;
	private CouponBenefitOption benefitOption;
	private Long benefitValue;
	private CouponLicenseGradeMatchingType couponLicenseGradeMatchingType;
	private CouponRecurringIntervalMatchingType couponRecurringIntervalMatchingType;

	private CouponSendDto(
		Long id, String code, String name, String description, CouponType couponType, Long maxCount,
		ZonedDateTime expiredDate, ApprovalStatus status, UseStatus useStatus, CouponBenefitOption benefitOption, Long benefitValue,
		CouponLicenseGradeMatchingType couponLicenseGradeMatchingType, CouponRecurringIntervalMatchingType couponRecurringIntervalMatchingType
	) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.description = description;
		this.couponType = couponType;
		this.maxCount = maxCount;
		this.expiredDate = expiredDate;
		this.status = status;
		this.useStatus = useStatus;
		this.benefitOption = benefitOption;
		this.benefitValue = benefitValue;
		this.couponLicenseGradeMatchingType = couponLicenseGradeMatchingType;
		this.couponRecurringIntervalMatchingType = couponRecurringIntervalMatchingType;
	}

	public static CouponSendDto of(Coupon coupon) {
		return new CouponSendDto(
			coupon.getId(), coupon.getCode(), coupon.getName(), coupon.getDescription(), coupon.getCouponType(),
			coupon.getMaxCount(), coupon.getExpiredDate(), coupon.getStatus(), coupon.getUseStatus(),
			coupon.getBenefitOption(), coupon.getBenefitValue(), coupon.getCouponLicenseGradeMatchingType(),
			coupon.getCouponRecurringIntervalMatchingType()
		);
	}
}
