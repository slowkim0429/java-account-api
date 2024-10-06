package com.virnect.account.port.outbound;

import java.util.Optional;

import com.virnect.account.domain.model.ServiceRegion;

public interface RegionRepositoryCustom {
	Optional<ServiceRegion> getRegion(Long regionId);
}
