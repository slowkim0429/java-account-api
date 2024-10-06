package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QControlModeAccessHistory.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.ControlModeAccessHistoryResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QControlModeAccessHistoryResponseDto;
import com.virnect.account.port.outbound.ControlModeAccessHistoryRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class ControlModeAccessHistoryRepositoryImpl implements ControlModeAccessHistoryRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ControlModeAccessHistoryResponseDto> getControlModeAccessHistories(Pageable pageable) {
		QueryResults<ControlModeAccessHistoryResponseDto> pagingResult = queryFactory
			.select(new QControlModeAccessHistoryResponseDto(
					controlModeAccessHistory.id,
					controlModeAccessHistory.accessResultType,
					controlModeAccessHistory.createdBy,
					controlModeAccessHistory.createdDate,
					controlModeAccessHistory.lastModifiedBy,
					controlModeAccessHistory.updatedDate
				)
			).from(controlModeAccessHistory)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(controlModeAccessHistory.id.desc())
			.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}
}
