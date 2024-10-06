package com.virnect.account.port.outbound;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.response.ControlModeAccessHistoryResponseDto;

public interface ControlModeAccessHistoryRepositoryCustom {

	Page<ControlModeAccessHistoryResponseDto> getControlModeAccessHistories(Pageable pageable);
}
