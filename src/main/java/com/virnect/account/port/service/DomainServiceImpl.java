package com.virnect.account.port.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.domain.DomainSearchDto;
import com.virnect.account.adapter.inbound.dto.response.DomainResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.model.Domain;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.DomainService;
import com.virnect.account.port.outbound.DomainRepository;

@Service
@RequiredArgsConstructor
public class DomainServiceImpl implements DomainService {
	private final DomainRepository domainRepository;

	@Override
	public PageContentResponseDto<DomainResponseDto> getDomains(
		DomainSearchDto domainSearchDto, Pageable pageable
	) {
		Page<DomainResponseDto> domains = domainRepository.getDomainResponses(domainSearchDto, pageable);
		return new PageContentResponseDto<>(domains, pageable);
	}

	@Override
	public Domain getDomain(Long regionId, String recordName) {
		return domainRepository.getDomainByRegionIdAndRecordName(regionId, recordName)
			.orElseThrow(() -> new CustomException(
				ErrorCode.NOT_FOUND_DOMAIN));
	}

}
