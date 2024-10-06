package com.virnect.account.port.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.locale.LocaleSearchDto;
import com.virnect.account.adapter.inbound.dto.response.LocaleResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.StateResponseDto;
import com.virnect.account.port.inbound.LocaleService;
import com.virnect.account.port.outbound.LocaleRepository;
import com.virnect.account.port.outbound.StateRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LocaleServiceImpl implements LocaleService {
	private final LocaleRepository localeRepository;
	private final StateRepository stateRepository;
	private static final String LOCALE_CODE_OF_STATES = "US|CA";

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<LocaleResponseDto> getLocales(
		LocaleSearchDto localeSearchDto, Pageable pageable
	) {
		Page<LocaleResponseDto> locales = localeRepository.getLocalesResponse(localeSearchDto, pageable);
		for (LocaleResponseDto locale : locales) {
			if (locale.getCode().matches(LOCALE_CODE_OF_STATES)) {
				List<StateResponseDto> states = stateRepository.getStateResponses(locale.getCode());
				locale.setStates(states);
			}
		}
		return new PageContentResponseDto<>(locales, pageable);
	}
}
