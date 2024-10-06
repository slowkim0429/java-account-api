package com.virnect.account.port.inbound;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserSearchDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserStatisticsSearchDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdateImageRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.JoinedUserStatisticsResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserStatisticsResponseDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.User;
import com.virnect.account.security.service.CustomUserDetails;

public interface UserService {
	UserResponseDto getUserByCurrent(CustomUserDetails customUserDetails);

	boolean existsUserByEmail(String email);

	UserResponseDto getUserResponseDtoByEmail(String email);

	UserResponseDto getUserById(Long id);

	PageContentResponseDto<UserResponseDto> getUsers(UserSearchDto userSearchDto, Pageable pageable);

	void setUserRole(Long userId, Role role, UseStatus useStatus);

	void passwordVerification(PasswordVerificationRequestDto passwordVerificationRequestDto);

	void updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto);

	void updateCurrentUserPassword(UserUpdatePasswordRequestDto updatePasswordRequestDto);

	User getUserByEmail(String email);

	String createInitialNickname(String email);

	void update(UserUpdateRequestDto userUpdateRequestDto);

	void updateCurrentUserProfileImage(UserUpdateImageRequestDto userUpdateImageRequestDto);

	void deleteCurrentUserProfileImage();

	User getUserByOrganizationId(Long organizationId);

	List<UserRevisionResponseDto> getUserRevision(Long userId);

	void sync(Long userId);

	UserStatisticsResponseDto getStatistics(UserStatisticsSearchDto userStatisticsSearchDto);

	JoinedUserStatisticsResponseDto getStatisticsByJoinedUser();

	void updateLastLoginDate(String email);
}
