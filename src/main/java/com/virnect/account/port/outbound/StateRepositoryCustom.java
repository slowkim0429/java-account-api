package com.virnect.account.port.outbound;

import java.util.List;
import java.util.Optional;

import com.virnect.account.adapter.inbound.dto.response.StateResponseDto;
import com.virnect.account.domain.model.ServiceLocaleState;

public interface StateRepositoryCustom {
	List<StateResponseDto> getStateResponses(String localeCode);

	Optional<ServiceLocaleState> getState(String code);
}
