package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.locale.LocaleSearchDto;
import com.virnect.account.adapter.inbound.dto.response.LocaleResponseDto;
import com.virnect.account.domain.model.ServiceRegionLocaleMapping;

public interface LocaleRepositoryCustom {
	Page<LocaleResponseDto> getLocalesResponse(LocaleSearchDto localeSearchDto, Pageable pageable);

	Optional<ServiceRegionLocaleMapping> getLocaleById(Long id);

	Optional<ServiceRegionLocaleMapping> getLocale(String code);
}
