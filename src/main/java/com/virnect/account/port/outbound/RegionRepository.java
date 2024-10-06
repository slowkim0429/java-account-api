package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.ServiceRegion;

public interface RegionRepository extends JpaRepository<ServiceRegion, Long>, RegionRepositoryCustom {
}
