package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.response.ControlModeAccessHistoryResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;

public interface ControlModeAccessHistoryService {

	PageContentResponseDto<ControlModeAccessHistoryResponseDto> getControlModeAccessHistories(Pageable pageable);

	void accessFailed();

	void accessSucceeded();
}
