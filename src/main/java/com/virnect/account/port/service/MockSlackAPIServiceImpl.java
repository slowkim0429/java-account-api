package com.virnect.account.port.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.account.domain.model.ErrorLog;
import com.virnect.account.port.inbound.SlackAPIService;

@Service
@RequiredArgsConstructor
@Profile({"!production"})
@Slf4j
public class MockSlackAPIServiceImpl implements SlackAPIService {

	@Override
	public void create(ErrorLog errorLog) {
		log.info("MockSlackAPIServiceImpl.create");
	}

}
