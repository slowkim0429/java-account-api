package com.virnect.account.port.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideRequestDto;
import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UpdateGuideResponseDto;
import com.virnect.account.domain.model.UpdateGuide;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.UpdateGuideService;
import com.virnect.account.port.outbound.UpdateGuideRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateGuideServiceImpl implements UpdateGuideService {

	private final UpdateGuideRepository updateGuideRepository;

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<UpdateGuideResponseDto> getUpdateGuides(
		UpdateGuideSearchDto updateGuideSearchDto, Pageable pageable
	) {
		Page<UpdateGuideResponseDto> updateGuides = updateGuideRepository.getUpdateGuides(
			updateGuideSearchDto, pageable);
		return new PageContentResponseDto<>(updateGuides, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public UpdateGuideResponseDto getUpdateGuideResponse(Long updateGuideId) {
		return updateGuideRepository.getUpdateGuideResponse(updateGuideId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_UPDATE_GUIDE));
	}

	@Override
	public UpdateGuideResponseDto getLatestUpdateGuide(String serviceType) {
		return updateGuideRepository.getUpdateGuideResponse(serviceType, true)
			.orElse(null);
	}

	@Override
	public void create(UpdateGuideRequestDto updateGuideRequestDto) {
		updateGuideRepository.save(UpdateGuide.from(updateGuideRequestDto));
	}

	@Override
	public void update(Long updateGuideId, UpdateGuideRequestDto updateGuideRequestDto) {
		UpdateGuide requestedUpdateGuide = updateGuideRepository.getUpdateGuide(updateGuideId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_UPDATE_GUIDE));
		requestedUpdateGuide.update(updateGuideRequestDto);
	}

	@Override
	public void updateExposure(Long updateGuideId, Boolean isExposed) {
		UpdateGuide persistentUpdateGuide = updateGuideRepository.getUpdateGuide(updateGuideId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_UPDATE_GUIDE));
		persistentUpdateGuide.updateExposure(isExposed);
	}
}
