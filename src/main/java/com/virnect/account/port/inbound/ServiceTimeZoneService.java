package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ServiceTimeZoneResponseDto;

public interface ServiceTimeZoneService {
	PageContentResponseDto<ServiceTimeZoneResponseDto> getServiceTimeZones(Pageable pageable);
}
