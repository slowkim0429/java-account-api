package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.domain.DomainSearchDto;
import com.virnect.account.adapter.inbound.dto.response.DomainResponseDto;
import com.virnect.account.domain.model.Domain;

public interface DomainRepositoryCustom {
	Optional<Domain> getDomainByRegionIdAndRecordName(Long regionId, String recordName);

	Page<DomainResponseDto> getDomainResponses(DomainSearchDto domainSearchDto, Pageable pageable);
}