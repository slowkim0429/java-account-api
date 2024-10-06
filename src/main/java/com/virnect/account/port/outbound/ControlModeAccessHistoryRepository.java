package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.ControlModeAccessHistory;

public interface ControlModeAccessHistoryRepository
	extends JpaRepository<ControlModeAccessHistory, Long>, ControlModeAccessHistoryRepositoryCustom {
}
