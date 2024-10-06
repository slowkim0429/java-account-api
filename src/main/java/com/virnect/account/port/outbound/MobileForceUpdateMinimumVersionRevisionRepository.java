package com.virnect.account.port.outbound;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionRevisionResponseDto;

public interface MobileForceUpdateMinimumVersionRevisionRepository {

	Page<MobileForceUpdateMinimumVersionRevisionResponseDto> getMobileForceUpdateMinimumVersionRevisions(
		Pageable pageable
	);
}
