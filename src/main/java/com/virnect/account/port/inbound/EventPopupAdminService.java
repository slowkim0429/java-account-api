package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupSearchDto;
import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;

public interface EventPopupAdminService {

	PageContentResponseDto<EventPopupAdminResponseDto> getEventPopups(
		EventPopupSearchDto eventPopupSearchDto, Pageable pageable
	);

	void create(EventPopupCreateRequestDto eventPopupCreateRequestDto);

	void changeExpose(Long eventPopupId, Boolean isExposed);

	void update(Long eventPopupId, EventPopupUpdateRequestDto eventPopupUpdateRequestDto);

	EventPopupAdminResponseDto get(Long eventPopupId);

	PageContentResponseDto<EventPopupRevisionResponseDto> getEventPopupRevisions(Long eventPopupId, Pageable pageable);
}
