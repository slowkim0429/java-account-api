package com.virnect.account.port.outbound;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.response.ExternalServiceMappingRevisionResponseDto;

public interface ExternalServiceMappingRevisionRepository {

	Page<ExternalServiceMappingRevisionResponseDto> getRevisions(Long externalServiceMappingId, Pageable pageable);
}
