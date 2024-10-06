package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QServiceLocaleState.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.QStateResponseDto;
import com.virnect.account.adapter.inbound.dto.response.StateResponseDto;
import com.virnect.account.domain.model.ServiceLocaleState;
import com.virnect.account.port.outbound.StateRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class StateRepositoryImpl implements StateRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<StateResponseDto> getStateResponses(String localeCode) {
		return
			queryFactory.select(new QStateResponseDto(
						serviceLocaleState.id
						, serviceLocaleState.name
						, serviceLocaleState.code
					)
				)
				.from(serviceLocaleState)
				.where(
					serviceLocaleState.localeCode.eq(localeCode)
				)
				.orderBy(serviceLocaleState.id.asc())
				.fetch();
	}

	@Override
	public Optional<ServiceLocaleState> getState(String code) {
		return Optional.ofNullable(
			queryFactory.selectFrom(serviceLocaleState)
				.where(
					serviceLocaleState.code.eq(code)
				)
				.fetchOne()
		);
	}
}
