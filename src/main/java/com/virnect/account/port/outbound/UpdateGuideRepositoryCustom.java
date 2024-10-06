package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideSearchDto;
import com.virnect.account.adapter.inbound.dto.response.UpdateGuideResponseDto;
import com.virnect.account.domain.model.UpdateGuide;

public interface UpdateGuideRepositoryCustom {

	Page<UpdateGuideResponseDto> getUpdateGuides(UpdateGuideSearchDto updateGuideSearchDto, Pageable pageable);

	Optional<UpdateGuideResponseDto> getUpdateGuideResponse(String serviceType, Boolean isExposed);

	Optional<UpdateGuide> getUpdateGuide(Long updateGuideId);

	Optional<UpdateGuideResponseDto> getUpdateGuideResponse(Long updateGuideId);
}
