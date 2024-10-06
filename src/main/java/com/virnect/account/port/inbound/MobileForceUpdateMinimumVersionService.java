package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.appversion.MobileForceUpdateMinimumVersionUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;

public interface MobileForceUpdateMinimumVersionService {

	MobileForceUpdateMinimumVersionResponseDto getForceUpdateMinimumVersion();

	MobileForceUpdateMinimumVersionAdminResponseDto getForceUpdateMinimumVersionByAdmin();

	void updateForceUpdateMinimumVersion(
		MobileForceUpdateMinimumVersionUpdateRequestDto mobileForceUpdateMinimumVersionUpdateRequestDto
	);

	PageContentResponseDto<MobileForceUpdateMinimumVersionRevisionResponseDto> getMobileForceUpdateMinimumVersionRevisions(
		Pageable pageable
	);
}
