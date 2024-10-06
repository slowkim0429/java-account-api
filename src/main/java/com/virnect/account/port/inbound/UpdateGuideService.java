package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideRequestDto;
import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UpdateGuideResponseDto;

public interface UpdateGuideService {

	PageContentResponseDto<UpdateGuideResponseDto> getUpdateGuides(
		UpdateGuideSearchDto updateGuideSearchDto, Pageable pageable
	);

	UpdateGuideResponseDto getUpdateGuideResponse(Long updateGuideId);

	UpdateGuideResponseDto getLatestUpdateGuide(String serviceType);

	void create(UpdateGuideRequestDto updateGuideRequestDto);

	void update(Long updateGuideId, UpdateGuideRequestDto updateGuideRequestDto);

	void updateExposure(Long updateGuideId, Boolean isExposed);
}
