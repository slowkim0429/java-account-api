package com.virnect.account.port.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.appversion.MobileForceUpdateMinimumVersionUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.model.MobileForceUpdateMinimumVersion;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.MobileForceUpdateMinimumVersionService;
import com.virnect.account.port.outbound.MobileForceUpdateMinimumVersionRepository;
import com.virnect.account.port.outbound.MobileForceUpdateMinimumVersionRevisionRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class MobileForceUpdateMinimumVersionServiceImpl implements MobileForceUpdateMinimumVersionService {

	private final MobileForceUpdateMinimumVersionRepository mobileForceUpdateMinimumVersionRepository;
	private final MobileForceUpdateMinimumVersionRevisionRepository mobileForceUpdateMinimumVersionRevisionRepository;

	@Transactional(readOnly = true)
	@Override
	public MobileForceUpdateMinimumVersionResponseDto getForceUpdateMinimumVersion() {
		return mobileForceUpdateMinimumVersionRepository.getForceUpdateMinimumVersionResponse()
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MOBILE_FORCE_UPDATE_MINIMUM_VERSION));
	}

	@Transactional(readOnly = true)
	@Override
	public MobileForceUpdateMinimumVersionAdminResponseDto getForceUpdateMinimumVersionByAdmin() {
		return mobileForceUpdateMinimumVersionRepository.getForceUpdateMinimumVersionByAdmin()
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MOBILE_FORCE_UPDATE_MINIMUM_VERSION));
	}

	@Override
	public void updateForceUpdateMinimumVersion(
		MobileForceUpdateMinimumVersionUpdateRequestDto mobileForceUpdateMinimumVersionUpdateRequestDto
	) {
		MobileForceUpdateMinimumVersion mobileForceUpdateMinimumVersion = mobileForceUpdateMinimumVersionRepository.getForceUpdateMinimumVersion()
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MOBILE_FORCE_UPDATE_MINIMUM_VERSION));
		mobileForceUpdateMinimumVersion.update(mobileForceUpdateMinimumVersionUpdateRequestDto);
	}

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<MobileForceUpdateMinimumVersionRevisionResponseDto> getMobileForceUpdateMinimumVersionRevisions(
		Pageable pageable
	) {
		Page<MobileForceUpdateMinimumVersionRevisionResponseDto> mobileForceUpdateMinimumVersionRevisions = mobileForceUpdateMinimumVersionRevisionRepository.getMobileForceUpdateMinimumVersionRevisions(
			pageable);
		return new PageContentResponseDto<>(mobileForceUpdateMinimumVersionRevisions, pageable);
	}
}
