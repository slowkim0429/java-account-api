package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.domain.DomainSearchDto;
import com.virnect.account.adapter.inbound.dto.response.DomainResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.model.Domain;

public interface DomainService {
	PageContentResponseDto<DomainResponseDto> getDomains(DomainSearchDto domainSearchDto, Pageable pageable);

	Domain getDomain(Long regionId, String recordName);
}
