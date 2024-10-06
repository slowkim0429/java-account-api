package com.virnect.account.domain.model;

import java.time.ZonedDateTime;

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

import com.virnect.account.adapter.inbound.dto.request.coupon.CouponCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponModifyRequestDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CouponBenefitOption;
import com.virnect.account.domain.enumclass.CouponLicenseGradeMatchingType;
import com.virnect.account.domain.enumclass.CouponRecurringIntervalMatchingType;
import com.virnect.account.domain.enumclass.CouponType;
import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Audited
@Getter
@Table(name = "coupons",
	indexes = {
		@Index(name = "idx_coupon_type", columnList = "coupon_type"),
		@Index(name = "idx_status", columnList = "status"),
		@Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_code", columnList = "code")
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "coupon_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @Column(name = "benefit_option", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CouponBenefitOption benefitOption;

    @Column(name = "benefit_value", nullable = false)
    private Long benefitValue;

    @Column(name = "coupon_license_grade_matching_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CouponLicenseGradeMatchingType couponLicenseGradeMatchingType;

    @Column(name = "coupon_recurring_interval_matching_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CouponRecurringIntervalMatchingType couponRecurringIntervalMatchingType;

    @Column(name = "max_count", nullable = false)
    private Long maxCount;

    @Column(name = "expired_at", nullable = false)
    private ZonedDateTime expiredDate;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status = ApprovalStatus.REGISTER;

    @Column(name = "use_status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UseStatus useStatus = UseStatus.UNUSE;

    private Coupon(
        String code, String name, String description, CouponType couponType, Long maxCount, ZonedDateTime expiredDate,
        CouponBenefitOption benefitOption, Long benefitValue,
        CouponLicenseGradeMatchingType couponLicenseGradeMatchingType,
        CouponRecurringIntervalMatchingType couponRecurringIntervalMatchingType
    ) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.couponType = couponType;
        this.maxCount = maxCount;
        this.expiredDate = expiredDate;
        this.benefitOption = benefitOption;
        this.benefitValue = benefitValue;
        this.couponLicenseGradeMatchingType = couponLicenseGradeMatchingType;
        this.couponRecurringIntervalMatchingType = couponRecurringIntervalMatchingType;
    }

    public static Coupon create(CouponCreateRequestDto requestDto) {
        return new Coupon(
            requestDto.getCode(), requestDto.getName(), requestDto.getDescription(),
            requestDto.couponTypeValueOf(), requestDto.getMaxCount(), requestDto.getExpiredDate(),
            requestDto.benefitOptionValueOf(), requestDto.getBenefitValue(),
            requestDto.couponLicenseGradeMatchingTypeValueOf(), requestDto.couponRecurringIntervalMatchingTypeValueOf()
        );
    }

    public void updateStatus(ApprovalStatus status) {
        this.status = status;
    }

    public void updateUseStatus(UseStatus useStatus) {
        this.useStatus = useStatus;
    }

    public void update(CouponModifyRequestDto requestDto) {
        this.code = requestDto.getCode();
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.maxCount = requestDto.getMaxCount();
        this.expiredDate = requestDto.getExpiredDate();
        this.couponType = requestDto.couponTypeValueOf();
        this.benefitOption = requestDto.benefitOptionValueOf();
        this.benefitValue = requestDto.getBenefitValue();
        this.couponLicenseGradeMatchingType = requestDto.couponLicenseGradeMatchingTypeValueOf();
        this.couponRecurringIntervalMatchingType = requestDto.couponRecurringIntervalMatchingTypeValueOf();
    }
}
