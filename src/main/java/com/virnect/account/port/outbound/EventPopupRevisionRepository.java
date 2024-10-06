package com.virnect.account.port.outbound;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.EventPopupRevisionResponseDto;

@Repository
public interface EventPopupRevisionRepository {

	Page<EventPopupRevisionResponseDto> getEventPopupRevisionResponses(Long eventPopupId, Pageable pageable);
}
