package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.hubspot.ExternalServiceMappingSearchDto;
import com.virnect.account.adapter.inbound.dto.response.HubspotMappingResponseDto;
import com.virnect.account.domain.enumclass.InternalDomain;
import com.virnect.account.domain.model.ExternalServiceMapping;

public interface ExternalServiceMappingRepositoryCustom {
	Optional<ExternalServiceMapping> getExternalServiceMapping(
		Long id, InternalDomain internalDomain, Long internalMappingId
	);

	Page<HubspotMappingResponseDto> getExternalServiceMappingResponses(
		ExternalServiceMappingSearchDto externalServiceMappingSearchDto, Pageable pageable
	);
}
