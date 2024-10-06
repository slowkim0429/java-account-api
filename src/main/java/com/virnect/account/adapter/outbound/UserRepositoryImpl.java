package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QServiceRegionLocaleMapping.*;
import static com.virnect.account.domain.model.QUser.*;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.user.UserSearchDto;
import com.virnect.account.adapter.inbound.dto.response.JoinedUserStatisticsResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QJoinedUserStatisticsResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QUserResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QUserStatisticsResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserStatisticsResponseDto;
import com.virnect.account.domain.enumclass.AcceptOrReject;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.User;
import com.virnect.account.port.outbound.UserRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
	private final JPAQueryFactory query;

	private BooleanExpression eqId(Long id) {
		if (id == null || id <= 0) {
			return null;
		}
		return user.id.eq(id);
	}

	private BooleanExpression eqEmail(String email) {
		if (StringUtils.isEmpty(email)) {
			return null;
		}
		return user.email.eq(email);
	}

	private BooleanExpression startWithEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return null;
		}
		return user.email.startsWith(email);
	}

	private BooleanExpression startsWithEmailDomain(String emailDomain) {
		if (StringUtils.isBlank(emailDomain)) {
			return null;
		}
		return user.emailDomain.startsWith(emailDomain);
	}

	private BooleanExpression startsWithLocaleName(String localeName) {
		if (StringUtils.isBlank(localeName)) {
			return null;
		}
		return serviceRegionLocaleMapping.name.toLowerCase().startsWith(localeName.toLowerCase());
	}

	private BooleanExpression eqRegionCode(String regionCode) {
		if (StringUtils.isBlank(regionCode)) {
			return null;
		}
		return user.regionCode.eq(regionCode);
	}

	private BooleanExpression eqMarketInfoReceive(String marketInfoReceive) {
		if (StringUtils.isBlank(marketInfoReceive)) {
			return null;
		}
		return user.marketInfoReceive.eq(AcceptOrReject.valueOf(marketInfoReceive));
	}

	private BooleanExpression eqNickname(String nickname) {
		if (StringUtils.isEmpty(nickname)) {
			return null;
		}
		return user.nickname.eq(nickname);
	}

	private BooleanExpression eqOrganizationId(Long organizationId) {
		if (organizationId == null || organizationId <= 0) {
			return null;
		}
		return user.organizationId.eq(organizationId);
	}

	private BooleanExpression eqOrganizationStatus(UseStatus status) {
		if (status == null || UseStatus.NONE.equals(status)) {
			return null;
		}
		return user.organizationStatus.eq(status);
	}

	private BooleanExpression eqRegionId(Long regionId) {
		if (regionId == null || regionId <= 0) {
			return null;
		}
		return user.regionId.eq(regionId);
	}

	private BooleanExpression eqLocaleId(Long localeId) {
		if (localeId == null || localeId <= 0) {
			return null;
		}
		return user.localeId.eq(localeId);
	}

	private BooleanExpression eqStatus(MembershipStatus status) {
		if (status == null) {
			return null;
		}
		return user.status.eq(status);
	}

	private BooleanExpression eqHubspotContactId(Long hubspotContractId) {
		if (hubspotContractId == null) {
			return null;
		}
		return user.hubSpotContactId.eq(hubspotContractId);
	}

	private BooleanExpression goeCreatedDate(ZonedDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return user.createdDate.goe(dateTime);
	}

	private BooleanExpression loeCreatedDate(ZonedDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return user.createdDate.loe(dateTime);
	}

	private BooleanExpression goeUpdatedDate(ZonedDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return user.updatedDate.goe(dateTime);
	}

	private BooleanExpression loeUpdatedDate(ZonedDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return user.updatedDate.loe(dateTime);
	}

	@Override
	public Optional<User> getUser(String email) {
		return Optional.ofNullable(
			query.selectFrom(user)
				.where(
					eqEmail(email),
					user.status.eq(MembershipStatus.JOIN)
				)
				.fetchOne());
	}

	@Override
	public boolean existsUserByEmail(String email) {
		Integer userId = query.selectOne().from(user)
			.where(
				user.email.eq(email),
				user.status.eq(MembershipStatus.JOIN)
			).fetchFirst();
		return userId != null;
	}

	@Override
	public Optional<User> getJoinUserById(Long id) {
		return Optional.ofNullable(
			query.selectFrom(user)
				.where(
					eqId(id),
					user.status.eq(MembershipStatus.JOIN)
				)
				.fetchOne());
	}

	@Override
	public Optional<User> getUser(Long id) {
		return Optional.ofNullable(
			query.selectFrom(user)
				.where(
					eqId(id)
				)
				.fetchOne());
	}

	@Override
	public Optional<UserResponseDto> getUserResponse(Long userId) {
		return Optional.ofNullable(
			query.select(
					new QUserResponseDto(
						user.id
						, user.nickname
						, user.email
						, user.profileImage
						, user.profileColor
						, user.marketInfoReceive
						, user.language
						, user.organizationId
						, user.organizationStatus
						, user.createdDate
						, user.updatedDate
						, user.createdBy
						, user.lastModifiedBy
						, user.localeCode
						, user.localeId
						, user.status
						, user.hubSpotContactId
						, user.privacyPolicy
						, user.regionAwsCode
						, user.regionCode
						, user.regionId
						, user.termsOfService
						, user.referrerUrl
						, user.lastLoginDate
						, user.zoneId
					)
				)
				.from(user)
				.where(
					eqId(userId)
				)
				.fetchOne());
	}

	@Override
	public Page<UserResponseDto> getUsers(
		UserSearchDto userSearchDto, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable
	) {
		QueryResults<UserResponseDto> pagingResult = query
			.select(new QUserResponseDto(
					user.id
					, user.nickname
					, user.email
					, user.profileImage
					, user.profileColor
					, user.marketInfoReceive
					, user.language
					, user.organizationId
					, user.organizationStatus
					, user.createdDate
					, user.updatedDate
					, user.createdBy
					, user.lastModifiedBy
					, user.localeCode
					, user.localeId
					, user.status
					, user.hubSpotContactId
					, user.privacyPolicy
					, user.regionAwsCode
					, user.regionCode
					, user.regionId
					, user.termsOfService
					, user.referrerUrl
					, user.lastLoginDate
					, user.zoneId
				)
			)
			.from(user)
			.join(serviceRegionLocaleMapping).on(user.localeId.eq(serviceRegionLocaleMapping.id))
			.where(
				eqId(userSearchDto.getId()),
				eqHubspotContactId(userSearchDto.getHubspotContactId()),
				startWithEmail(userSearchDto.getEmail()),
				startsWithEmailDomain(userSearchDto.getEmailDomain()),
				eqRegionCode(userSearchDto.getRegionCode()),
				eqMarketInfoReceive(userSearchDto.getMarketInfoReceive()),
				eqNickname(userSearchDto.getNickname()),
				eqOrganizationId(userSearchDto.getOrganizationId()),
				eqOrganizationStatus(userSearchDto.getOrganizationStatus()),
				eqRegionId(userSearchDto.getRegionId()),
				eqLocaleId(userSearchDto.getLocaleId()),
				eqStatus(userSearchDto.getStatus()),
				startsWithReferrerUrl(userSearchDto.getReferrerUrl()),
				startsWithLocaleName(userSearchDto.getLocaleName()),
				eqStatus(userSearchDto.getStatus()),
				goeCreatedDate(startDate),
				loeCreatedDate(endDate)
			)
			.orderBy(user.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}

	private BooleanExpression startsWithReferrerUrl(String referrerUrl) {
		if (StringUtils.isBlank(referrerUrl)) {
			return null;
		}
		return user.referrerUrl.startsWith(referrerUrl);
	}

	private BooleanExpression betweenCreatedDateOrBetweenUpdatedDate(ZonedDateTime startDate, ZonedDateTime endDate) {
		if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
			return null;
		}
		return (goeCreatedDate(startDate).and(loeCreatedDate(endDate)))
			.or(goeUpdatedDate(startDate).and(loeUpdatedDate(endDate)));
	}

	@Override
	public Optional<User> getUserByOrganizationId(Long organizationId) {
		return Optional.ofNullable(
			query
				.selectFrom(user)
				.where(
					user.organizationId.eq(organizationId),
					user.status.eq(MembershipStatus.JOIN)
				)
				.fetchOne()
		);
	}

	@Override
	public Optional<User> getUser(Long organizationId, MembershipStatus membershipStatus) {
		return Optional.ofNullable(
			query
				.selectFrom(user)
				.where(
					user.organizationId.eq(organizationId),
					eqStatus(membershipStatus)
				)
				.fetchOne());
	}

	@Override
	public UserStatisticsResponseDto getStatistics(ZonedDateTime startDate, ZonedDateTime endDate) {
		return query
			.select(new QUserStatisticsResponseDto(
					Expressions.cases()
						.when(
							user.status.eq(MembershipStatus.JOIN)
								.or(user.status.eq(MembershipStatus.RESIGN))
								.and(goeCreatedDate(startDate))
								.and(loeCreatedDate(endDate))
						)
						.then(1L)
						.otherwise(0L)
						.sum(),
					Expressions.cases()
						.when(
							user.status.eq(MembershipStatus.RESIGN)
								.and(goeUpdatedDate(startDate))
								.and(loeUpdatedDate(endDate))
						)
						.then(1L)
						.otherwise(0L)
						.sum()
				)
			)
			.where(
				betweenCreatedDateOrBetweenUpdatedDate(startDate, endDate)
			)
			.from(user)
			.fetchOne();
	}

	@Override
	public JoinedUserStatisticsResponseDto getStatisticsByJoinedUser() {
		return query
			.select(new QJoinedUserStatisticsResponseDto(
					user.count()
				)
			)
			.from(user)
			.where(user.status.eq(MembershipStatus.JOIN))
			.fetchOne();
	}
}
