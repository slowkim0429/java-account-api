package com.virnect.account.port.outbound;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.CouponRevisionResponseDto;

@Repository
public interface CouponRevisionRepository {
	Page<CouponRevisionResponseDto> getCouponRevisionResponses(Long couponId, Pageable pageable);
}
