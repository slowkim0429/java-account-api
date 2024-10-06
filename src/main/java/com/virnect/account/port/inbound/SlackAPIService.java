package com.virnect.account.port.inbound;

import org.springframework.scheduling.annotation.Async;

import com.virnect.account.domain.model.ErrorLog;

public interface SlackAPIService {
	@Async("asyncFeignTaskExecutor")
	void create(ErrorLog errorLog);
}
