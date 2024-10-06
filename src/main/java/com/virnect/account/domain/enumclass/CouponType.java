package com.virnect.account.domain.enumclass;

public enum CouponType {
    UPGRADE_LICENSE_ATTRIBUTE,
    UPGRADE_LICENSE_PERIOD;

    public boolean isUpgradeLicenseAttribute() {
        return this.equals(UPGRADE_LICENSE_ATTRIBUTE);
    }

}
