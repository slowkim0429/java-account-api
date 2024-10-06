package com.virnect.account.port.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ServiceTimeZoneResponseDto;
import com.virnect.account.port.inbound.ServiceTimeZoneService;
import com.virnect.account.port.outbound.ServiceTimeZoneRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceTimeZoneServiceImpl implements ServiceTimeZoneService {
	private final ServiceTimeZoneRepository serviceTimeZoneRepository;

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<ServiceTimeZoneResponseDto> getServiceTimeZones(Pageable pageable) {
		Page<ServiceTimeZoneResponseDto> serviceTimeZoneResponses = serviceTimeZoneRepository.getServiceTimeZoneResponses(
			pageable);
		return new PageContentResponseDto<>(serviceTimeZoneResponses, pageable);
	}
}
