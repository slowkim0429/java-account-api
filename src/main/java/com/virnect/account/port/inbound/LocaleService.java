package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.locale.LocaleSearchDto;
import com.virnect.account.adapter.inbound.dto.response.LocaleResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;

public interface LocaleService {
	PageContentResponseDto<LocaleResponseDto> getLocales(LocaleSearchDto localeSearchDto, Pageable pageable);
}
