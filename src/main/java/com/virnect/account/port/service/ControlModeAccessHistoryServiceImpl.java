package com.virnect.account.port.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.ControlModeAccessHistoryResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.model.ControlModeAccessHistory;
import com.virnect.account.port.inbound.ControlModeAccessHistoryService;
import com.virnect.account.port.outbound.ControlModeAccessHistoryRepository;

@Service
@RequiredArgsConstructor
public class ControlModeAccessHistoryServiceImpl implements ControlModeAccessHistoryService {

	private final ControlModeAccessHistoryRepository controlModeAccessHistoryRepository;

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<ControlModeAccessHistoryResponseDto> getControlModeAccessHistories(
		Pageable pageable
	) {
		Page<ControlModeAccessHistoryResponseDto> controlModeAccessHistories = controlModeAccessHistoryRepository.getControlModeAccessHistories(
			pageable);
		return new PageContentResponseDto<>(controlModeAccessHistories, pageable);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void accessFailed() {
		ControlModeAccessHistory failedHistory = ControlModeAccessHistory.createFailedHistory();
		controlModeAccessHistoryRepository.save(failedHistory);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void accessSucceeded() {
		ControlModeAccessHistory succeededHistory = ControlModeAccessHistory.createSucceededHistory();
		controlModeAccessHistoryRepository.save(succeededHistory);
	}
}
