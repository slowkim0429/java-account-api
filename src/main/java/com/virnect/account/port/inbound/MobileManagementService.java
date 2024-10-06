package com.virnect.account.port.inbound;

import com.virnect.account.adapter.inbound.dto.request.mobilemanagement.MobileManagementUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.MobileManagementNoticeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileManagementResponseDto;
import com.virnect.account.domain.model.MobileManagement;

public interface MobileManagementService {

	void passwordVerification(String password);

	MobileManagementNoticeResponseDto getExposedNotice();

	MobileManagementResponseDto getMobileManagementResponse();

	MobileManagement getMobileManagement();

	void update(MobileManagementUpdateRequestDto mobileManagementUpdateRequestDto);
}
