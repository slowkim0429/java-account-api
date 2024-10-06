package com.virnect.account.port.inbound;

import com.virnect.account.adapter.inbound.dto.request.eventpopup.SendCouponRequestDto;
import com.virnect.account.adapter.inbound.dto.response.EventPopupResponseDto;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;

public interface EventPopupService {

	EventPopupResponseDto getLatestEventPopup(EventType eventType, EventServiceType serviceType);

	void sendCouponEmail(Long eventPopupId, SendCouponRequestDto sendCouponRequestDto);
}
