package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QMobileForceUpdateMinimumVersion.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileForceUpdateMinimumVersionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QMobileForceUpdateMinimumVersionAdminResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QMobileForceUpdateMinimumVersionResponseDto;
import com.virnect.account.domain.model.MobileForceUpdateMinimumVersion;
import com.virnect.account.port.outbound.MobileForceUpdateMinimumVersionRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class MobileForceUpdateMinimumVersionRepositoryImpl implements MobileForceUpdateMinimumVersionRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<MobileForceUpdateMinimumVersionResponseDto> getForceUpdateMinimumVersionResponse() {
		return Optional.ofNullable(
			queryFactory.select(new QMobileForceUpdateMinimumVersionResponseDto(
					mobileForceUpdateMinimumVersion.version,
					mobileForceUpdateMinimumVersion.forceUpdateType
				)).from(mobileForceUpdateMinimumVersion)
				.fetchOne()
		);
	}

	@Override
	public Optional<MobileForceUpdateMinimumVersionAdminResponseDto> getForceUpdateMinimumVersionByAdmin() {
		return Optional.ofNullable(
			queryFactory.select(new QMobileForceUpdateMinimumVersionAdminResponseDto(
					mobileForceUpdateMinimumVersion.id,
					mobileForceUpdateMinimumVersion.bundleId,
					mobileForceUpdateMinimumVersion.version,
					mobileForceUpdateMinimumVersion.forceUpdateType,
					mobileForceUpdateMinimumVersion.createdBy,
					mobileForceUpdateMinimumVersion.createdDate,
					mobileForceUpdateMinimumVersion.lastModifiedBy,
					mobileForceUpdateMinimumVersion.updatedDate
				)).from(mobileForceUpdateMinimumVersion)
				.fetchOne()
		);
	}

	@Override
	public Optional<MobileForceUpdateMinimumVersion> getForceUpdateMinimumVersion() {
		return Optional.ofNullable(
			queryFactory.selectFrom(mobileForceUpdateMinimumVersion).fetchOne()
		);
	}
}
