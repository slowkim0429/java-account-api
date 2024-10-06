package com.virnect.account.port.outbound;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.user.UserSearchDto;
import com.virnect.account.adapter.inbound.dto.response.JoinedUserStatisticsResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserStatisticsResponseDto;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.model.User;

public interface UserRepositoryCustom {
	Optional<User> getUser(String email);

	boolean existsUserByEmail(String email);

	Optional<User> getJoinUserById(Long id);

	Optional<User> getUser(Long id);

	Optional<UserResponseDto> getUserResponse(Long userId);

	Page<UserResponseDto> getUsers(
		UserSearchDto userSearchDto, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable
	);

	Optional<User> getUserByOrganizationId(Long organizationId);

	Optional<User> getUser(Long organizationId, MembershipStatus membershipStatus);

	UserStatisticsResponseDto getStatistics(ZonedDateTime startDate, ZonedDateTime endDate);

	JoinedUserStatisticsResponseDto getStatisticsByJoinedUser();
}
