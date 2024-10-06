package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.response.ServiceTimeZoneResponseDto;
import com.virnect.account.domain.model.ServiceTimeZone;

public interface ServiceTimeZoneRepositoryCustom {
	Optional<ServiceTimeZone> getServiceTimeZone(String localeCode);

	Optional<ServiceTimeZone> getServiceTimeZone(String localeCode, String zoneId);

	Page<ServiceTimeZoneResponseDto> getServiceTimeZoneResponses(Pageable pageable);
}
