package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.EventPopup;

public interface EventPopupRepository extends JpaRepository<EventPopup, Long>, EventPopupRepositoryCustom {
}
