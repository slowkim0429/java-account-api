package com.virnect.account.port.outbound;

import java.util.Optional;

import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionResponseDto;
import com.virnect.account.domain.model.MobileForceUpdateMinimumVersion;

public interface MobileForceUpdateMinimumVersionRepositoryCustom {

	Optional<MobileForceUpdateMinimumVersionResponseDto> getForceUpdateMinimumVersionResponse();

	Optional<MobileForceUpdateMinimumVersionAdminResponseDto> getForceUpdateMinimumVersionByAdmin();

	Optional<MobileForceUpdateMinimumVersion> getForceUpdateMinimumVersion();
}
