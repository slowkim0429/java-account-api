package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom {
}
