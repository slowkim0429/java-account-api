package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupSearchDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupResponseDto;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;
import com.virnect.account.domain.model.EventPopup;

public interface EventPopupRepositoryCustom {

	Optional<EventPopupResponseDto> getEventPopupResponse(
		EventType eventType, EventServiceType serviceType, Boolean isExposed
	);

	Optional<EventPopupAdminResponseDto> getEventPopupAdminResponse(Long eventPopupId);

	Page<EventPopupAdminResponseDto> getEventPopupResponses(
		EventPopupSearchDto eventPopupSearchDto, Pageable pageable
	);

	Optional<EventPopup> getEventPopup(Long eventPopupId);

	Optional<EventPopup> getEventPopup(Boolean isExposed);

	Optional<EventPopup> getEventPopup(Long eventPopupId, EventType eventType, Boolean isExposed);
}
