package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.CouponDeliveryHistory;

public interface CouponDeliveryHistoryRepository
	extends JpaRepository<CouponDeliveryHistory, Long>, CouponDeliveryHistoryRepositoryCustom {
}
