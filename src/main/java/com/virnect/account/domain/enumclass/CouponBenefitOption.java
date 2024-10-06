package com.virnect.account.domain.enumclass;

public enum CouponBenefitOption {
	MAXIMUM_WORKSPACE(CouponType.UPGRADE_LICENSE_ATTRIBUTE),
	MAXIMUM_GROUP(CouponType.UPGRADE_LICENSE_ATTRIBUTE),
	MAXIMUM_GROUP_USER(CouponType.UPGRADE_LICENSE_ATTRIBUTE),
	STORAGE_SIZE_PER_MB(CouponType.UPGRADE_LICENSE_ATTRIBUTE),
	MAXIMUM_PROJECT(CouponType.UPGRADE_LICENSE_ATTRIBUTE),
	MAXIMUM_PUBLISHING_PROJECT(CouponType.UPGRADE_LICENSE_ATTRIBUTE),
	MAXIMUM_VIEW_PER_MONTH(CouponType.UPGRADE_LICENSE_ATTRIBUTE),

	YEAR(CouponType.UPGRADE_LICENSE_PERIOD),
	MONTH(CouponType.UPGRADE_LICENSE_PERIOD);

	private final CouponType couponType;

	CouponBenefitOption(CouponType couponType) {
		this.couponType = couponType;
	}

	public boolean isMatchedWithCouponType(CouponType couponType) {
		return this.couponType.equals(couponType);
	}
}
