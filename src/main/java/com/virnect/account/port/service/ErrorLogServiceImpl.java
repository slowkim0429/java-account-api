package com.virnect.account.port.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.error.ErrorLogCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.error.ErrorLogSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorLogDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorLogResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.model.ErrorLog;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.ErrorLogService;
import com.virnect.account.port.inbound.SlackAPIService;
import com.virnect.account.port.outbound.ErrorLogRepository;

@Component
@Transactional
@RequiredArgsConstructor
public class ErrorLogServiceImpl implements ErrorLogService {

	private final ErrorLogRepository errorLogRepository;
	private final SlackAPIService slackAPIService;

	@Override
	public void create(ErrorLog errorLog) {
		errorLogRepository.save(errorLog);
		slackAPIService.create(errorLog);
	}

	@Override
	public void create(ErrorLogCreateRequestDto errorLogCreateRequestDto) {
		ErrorLog errorLog = ErrorLog.clientErrorLog(errorLogCreateRequestDto);
		this.create(errorLog);
	}

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<ErrorLogResponseDto> getErrorLogs(
		ErrorLogSearchDto errorLogSearchDto, Pageable pageable
	) {

		Page<ErrorLogResponseDto> errorLogs = errorLogRepository.getErrorLogResponses(errorLogSearchDto, pageable);
		return new PageContentResponseDto<>(errorLogs, pageable);
	}

	@Transactional(readOnly = true)
	@Override
	public ErrorLogDetailResponseDto getErrorLog(Long errorLogId) {
		return errorLogRepository.getErrorLogResponse(errorLogId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
	}
}
