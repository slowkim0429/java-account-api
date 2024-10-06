package com.virnect.account.port.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.LicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.port.inbound.LicenseAttributeService;
import com.virnect.account.port.outbound.LicenseAttributeRepository;
import com.virnect.account.port.outbound.LicenseAttributeRevisionRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class LicenseAttributeServiceImpl implements LicenseAttributeService {

	private final LicenseAttributeRepository licenseAttributeRepository;
	private final LicenseAttributeRevisionRepository licenseAttributeRevisionRepository;

	@Transactional(readOnly = true)
	@Override
	public List<LicenseAttributeResponseDto> getLicenseAttributeResponse(Long licenseId) {
		return licenseAttributeRepository.getLicenseAttributeResponse(licenseId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<LicenseAdditionalAttributeResponseDto> getLicenseAdditionalAttributeResponse(
		Long licenseId
	) {
		return licenseAttributeRepository.getLicenseAdditionalAttributeResponse(licenseId);
	}

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<LicenseAttributeRevisionResponseDto> getLicenseAttributeRevisions(
		Long licenseId, LicenseAttributeType licenseAttributeType, Pageable pageable
	) {
		Page<LicenseAttributeRevisionResponseDto> licenseAttributeRevisionResponseDtos = licenseAttributeRevisionRepository.getLicenseAttributeRevisionResponses(
			licenseId, licenseAttributeType, pageable);
		return new PageContentResponseDto<>(licenseAttributeRevisionResponseDtos, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<LicenseAttributeRevisionResponseDto> getLicenseAdditionalAttributeRevisions(
		Long licenseId, LicenseAdditionalAttributeType licenseAdditionalAttributeType, Pageable pageable
	) {
		Page<LicenseAttributeRevisionResponseDto> licenseAttributeRevisionResponseDtos = licenseAttributeRevisionRepository.getLicenseAdditionalAttributeRevisions(
			licenseId, licenseAdditionalAttributeType, pageable);
		return new PageContentResponseDto<>(licenseAttributeRevisionResponseDtos, pageable);
	}
}
