package com.virnect.account.port.outbound;

import java.util.Optional;

import com.virnect.account.adapter.inbound.dto.response.MobileManagementNoticeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileManagementResponseDto;
import com.virnect.account.domain.model.MobileManagement;

public interface MobileManagementRepositoryCustom {
	Optional<MobileManagement> getMobileManagement();

	Optional<MobileManagementNoticeResponseDto> getMobileManagementNoticeResponse(boolean isExposed);

	Optional<MobileManagementResponseDto> getMobileManagementResponse();
}
