package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QMobileManagement.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.MobileManagementNoticeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QMobileManagementNoticeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileManagementResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QMobileManagementResponseDto;
import com.virnect.account.domain.model.MobileManagement;
import com.virnect.account.port.outbound.MobileManagementRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class MobileManagementRepositoryImpl implements MobileManagementRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<MobileManagement> getMobileManagement() {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(mobileManagement)
				.fetchOne()
		);
	}

	@Override
	public Optional<MobileManagementNoticeResponseDto> getMobileManagementNoticeResponse(boolean isExposed) {
		return Optional.ofNullable(
			queryFactory.select(
					new QMobileManagementNoticeResponseDto(
						mobileManagement.message
					)
				).from(mobileManagement)
				.where(mobileManagement.isExposed.eq(isExposed))
				.fetchOne()
		);
	}

	@Override
	public Optional<MobileManagementResponseDto> getMobileManagementResponse() {
		return Optional.ofNullable(
			queryFactory
				.select(new QMobileManagementResponseDto(
					mobileManagement.id,
					mobileManagement.message,
					mobileManagement.isExposed,
					mobileManagement.createdDate,
					mobileManagement.updatedDate,
					mobileManagement.createdBy,
					mobileManagement.lastModifiedBy
				)).from(mobileManagement)
				.fetchOne()
		);
	}
}
