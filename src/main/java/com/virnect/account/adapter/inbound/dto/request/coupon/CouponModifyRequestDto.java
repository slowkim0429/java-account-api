package com.virnect.account.adapter.inbound.dto.request.coupon;

import java.time.ZonedDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.CouponBenefitOption;
import com.virnect.account.domain.enumclass.CouponLicenseGradeMatchingType;
import com.virnect.account.domain.enumclass.CouponRecurringIntervalMatchingType;
import com.virnect.account.domain.enumclass.CouponType;

@Getter
@ApiModel
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponModifyRequestDto {
	@ApiModelProperty(value = "쿠폰명", example = "Happy new year Coupon", required = true)
	@NotBlank(message = "coupon name은 필수 값입니다.")
	@Size(max = 30)
	private String name;

	@ApiModelProperty(value = "쿠폰 코드", example = "ASsdDfLJ3ASK12JD4FL", required = true)
	@NotBlank(message = "coupon code는 필수 값입니다.")
	@Size(max = 30)
	private String code;

	@ApiModelProperty(value = "쿠폰을 사용할 수 있는 최대 횟수", example = "500", required = true)
	@NotNull(message = "max count는 필수 값입니다.")
	@Max(9999999999L)
	@Min(0)
	private Long maxCount;

	@ApiModelProperty(value = "쿠폰 유형", example = "UPGRADE_LICENSE_ATTRIBUTE", required = true)
	@NotBlank(message = "coupon type은 필수 값입니다.")
	@CommonEnum(enumClass = CouponType.class)
	private String couponType;

	@ApiModelProperty(value = "쿠폰 만료 기한", example = "2023-09-11 00:00:00", required = true)
	@NotNull(message = "expired date는 필수 값입니다.")
	private ZonedDateTime expiredDate;

	@ApiModelProperty(value = "쿠폰에 대한 설명", example = "This coupon is for this christmas", required = true)
	@NotBlank(message = "description은 필수 값입니다.")
	@Size(max = 255)
	private String description;

	@ApiModelProperty(value = "쿠폰을 통해 제공되는 옵션", example = "MAXIMUM_WORKSPACE", required = true)
	@NotBlank(message = "benefit option은 필수 값입니다.")
	@CommonEnum(enumClass = CouponBenefitOption.class)
	private String benefitOption;

	@ApiModelProperty(value = "쿠폰을 통해 추가로 제공되는 값", example = "10", required = true)
	@NotNull(message = "benefit value은 필수 값입니다.")
	@Positive
	private Long benefitValue;

	@ApiModelProperty(value = "쿠폰과 매칭되어야 하는 라이센스 등급", example = "[STANDARD, PROFESSIONAL, NONE]", required = true)
	@NotBlank(message = "coupon license grade matching type은 필수 값입니다.")
	@CommonEnum(enumClass = CouponLicenseGradeMatchingType.class)
	private String couponLicenseGradeMatchingType;

	@ApiModelProperty(value = "쿠폰과 매칭되어야 하는 구독 주기", example = "[YEAR, MONTH, NONE]", required = true)
	@NotBlank(message = "coupon recurring interval matching type은 필수 값입니다.")
	@CommonEnum(enumClass = CouponRecurringIntervalMatchingType.class)
	private String couponRecurringIntervalMatchingType;

	@ApiModelProperty(hidden = true)
	public boolean isExpiredDateValid() {
		return expiredDate.isAfter(ZonedDateTime.now());
	}

	@ApiModelProperty(hidden = true)
	public String getExpiredDateInvalidMessage() {
		return "expired date는 미래의 날짜만 입력 가능합니다.";
	}

	@ApiModelProperty(hidden = true)
	public boolean isBenefitOptionValid() {
		CouponType couponType = couponTypeValueOf();
		return benefitOptionValueOf().isMatchedWithCouponType(couponType);
	}

	@ApiModelProperty(hidden = true)
	public String getBenefitOptionInvalidMessage() {
		return "coupon type과 일치하지 않는 benefit option 입니다.";
	}

	private CouponModifyRequestDto(
		String name, String code, Long maxCount, String couponType, ZonedDateTime expiredDate, String description,
		String benefitOption, Long benefitValue, String couponLicenseGradeMatchingType,
		String couponRecurringIntervalMatchingType
	) {

		this.name = name;
		this.code = code;
		this.maxCount = maxCount;
		this.couponType = couponType;
		this.expiredDate = expiredDate;
		this.description = description;
		this.benefitOption = benefitOption;
		this.benefitValue = benefitValue;
		this.couponLicenseGradeMatchingType = couponLicenseGradeMatchingType;
		this.couponRecurringIntervalMatchingType = couponRecurringIntervalMatchingType;
	}

	public static CouponModifyRequestDto of(
		String name, String code, Long maxCount, String couponType, ZonedDateTime expiredDate, String description,
		String benefitOption, Long benefitValue, String couponLicenseGradeMatchingType,
		String couponRecurringIntervalMatchingType
	) {
		return new CouponModifyRequestDto(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);
	}

	@ApiModelProperty(hidden = true)
	public CouponType couponTypeValueOf() {
		return CouponType.valueOf(couponType);
	}

	@ApiModelProperty(hidden = true)
	public CouponBenefitOption benefitOptionValueOf() {
		return CouponBenefitOption.valueOf(benefitOption);
	}

	@ApiModelProperty(hidden = true)
	public CouponLicenseGradeMatchingType couponLicenseGradeMatchingTypeValueOf() {
		return CouponLicenseGradeMatchingType.valueOf(couponLicenseGradeMatchingType);
	}

	@ApiModelProperty(hidden = true)
	public CouponRecurringIntervalMatchingType couponRecurringIntervalMatchingTypeValueOf() {
		return CouponRecurringIntervalMatchingType.valueOf(couponRecurringIntervalMatchingType);
	}
}
