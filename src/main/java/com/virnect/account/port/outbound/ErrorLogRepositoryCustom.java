package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.error.ErrorLogSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorLogDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorLogResponseDto;

public interface ErrorLogRepositoryCustom {

	Optional<ErrorLogDetailResponseDto> getErrorLogResponse(Long errorLogId);

	Page<ErrorLogResponseDto> getErrorLogResponses(ErrorLogSearchDto errorLogSearchDto, Pageable pageable);
}
