package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.error.ErrorLogCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.error.ErrorLogSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorLogDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorLogResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.model.ErrorLog;

public interface ErrorLogService {
	void create(ErrorLog errorLog);

	void create(ErrorLogCreateRequestDto errorLogCreateRequestDto);

	PageContentResponseDto<ErrorLogResponseDto> getErrorLogs(
		ErrorLogSearchDto errorLogSearchDto, Pageable pageable
	);

	ErrorLogDetailResponseDto getErrorLog(Long errorLogId);
}
