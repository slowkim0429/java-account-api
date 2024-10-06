package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
import com.virnect.account.adapter.outbound.request.UserSendDto;
import com.virnect.account.domain.enumclass.AcceptOrReject;
import com.virnect.account.domain.enumclass.DateRangeType;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.ServiceRegion;
import com.virnect.account.domain.model.ServiceRegionLocaleMapping;
import com.virnect.account.domain.model.User;
import com.virnect.account.domain.model.UserRole;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.EmailAuthService;
import com.virnect.account.port.inbound.ExternalServiceMappingService;
import com.virnect.account.port.inbound.FileService;
import com.virnect.account.port.inbound.NotificationAPIService;
import com.virnect.account.port.inbound.SquarsApiService;
import com.virnect.account.port.inbound.UserService;
import com.virnect.account.port.inbound.WorkspaceAPIService;
import com.virnect.account.port.outbound.LocaleRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.RegionRepository;
import com.virnect.account.port.outbound.ServiceTimeZoneRepository;
import com.virnect.account.port.outbound.UserRepository;
import com.virnect.account.port.outbound.UserRevisionRepository;
import com.virnect.account.port.outbound.UserRoleRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.security.service.CustomUserDetails;
import com.virnect.account.security.service.CustomUserDetailsService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
	private final FileService fileService;
	private final CustomUserDetailsService customUserDetailsService;
	private final EmailAuthService emailAuthService;
	private final ExternalServiceMappingService externalServiceMappingService;
	private final WorkspaceAPIService workspaceAPIService;
	private final SquarsApiService squarsAPIService;
	private final NotificationAPIService notificationAPIService;

	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;
	private final LocaleRepository localeRepository;
	private final RedisRepository redisRepository;
	private final UserRevisionRepository userRevisionRepository;
	private final RegionRepository regionRepository;
	private final ServiceTimeZoneRepository serviceTimeZoneRepository;

	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public UserResponseDto getUserByCurrent(CustomUserDetails customUserDetails) {
		UserResponseDto userResponseDto = UserResponseDto.
			userCustomUserDetailsBuilder().
			customUserDetails(customUserDetails).
			build();

		User user = userRepository.getJoinUserById(customUserDetails.getId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));
		ServiceRegionLocaleMapping locale = localeRepository.getLocaleById(user.getLocaleId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LOCALE));
		userResponseDto.setLocaleName(locale.getName());
		return userResponseDto;
	}

	@Transactional(readOnly = true)
	public boolean existsUserByEmail(String email) {
		return userRepository.existsUserByEmail(email.toLowerCase());
	}

	@Transactional(readOnly = true)
	public UserResponseDto getUserResponseDtoByEmail(String email) {
		UserResponseDto userResponseDto = userRepository.getUser(email)
			.map(UserResponseDto::new)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		userResponseDto.setUserRoles(userRoleRepository.getUserUseRoles(userResponseDto.getId()));

		return userResponseDto;
	}

	@Transactional(readOnly = true)
	@Override
	public User getUserByEmail(String email) {
		User user = userRepository.getUser(email)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		if (!MembershipStatus.JOIN.equals(user.getStatus())) {
			throw new CustomException(NOT_FOUND_USER);
		}

		return user;
	}

	@Override
	public String createInitialNickname(String email) {
		String nickname = email.split("@")[0];
		return nickname.length() > 30 ? nickname.substring(0, 30) : nickname;
	}

	@Transactional(readOnly = true)
	public UserResponseDto getUserById(Long userId) {
		return userRepository.getUserResponse(userId).orElseThrow(
			() -> new CustomException(NOT_FOUND_USER));
	}

	@Transactional(readOnly = true)
	public PageContentResponseDto<UserResponseDto> getUsers(UserSearchDto userSearchDto, Pageable pageable) {
		final DateRangeType requestDateRange = userSearchDto.dateRangeTypeValueOf();
		ZonedDateTime startDate = requestDateRange.getStartDate();
		ZonedDateTime endDate = requestDateRange.getEndDate();
		if (requestDateRange.isCustom()) {
			startDate = userSearchDto.getStartDateOfCreatedDate();
			endDate = userSearchDto.getEndDateOfCreatedDate();
		}

		Page<UserResponseDto> userResponseDtos = userRepository.getUsers(userSearchDto, startDate, endDate, pageable);
		for (UserResponseDto userResponseDto : userResponseDtos) {
			List<UserRole> userRoles = userRoleRepository.getUserUseRoles(userResponseDto.getId());
			userResponseDto.setUserRoles(userRoles);
		}
		return new PageContentResponseDto<>(userResponseDtos, pageable);
	}

	@Override
	@Transactional
	public void update(UserUpdateRequestDto userUpdateRequestDto) {
		Long userId = SecurityUtil.getCurrentUserId();
		User currentUser = userRepository.getJoinUserById(userId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		if (StringUtils.isNotBlank(userUpdateRequestDto.getZoneId())) {
			serviceTimeZoneRepository.getServiceTimeZone(null, userUpdateRequestDto.getZoneId())
				.ifPresentOrElse(
					serviceTimeZone -> currentUser.updateZoneId(serviceTimeZone.getZoneId()),
					() -> {
						throw new CustomException(NOT_FOUND_SERVICE_TIME_ZONE);
					}
				);
		}

		if (StringUtils.isNotBlank(userUpdateRequestDto.getName())) {
			if (Objects.equals(currentUser.getNickname(), userUpdateRequestDto.getName())) {
				return;
			}
			currentUser.updateNickname(userUpdateRequestDto.getName());
			this.sendUpdateUserToWorkspaceAndSquarsAndNotification(currentUser);
		}

		if (userUpdateRequestDto.getLocaleId() != null) {
			if (Objects.equals(currentUser.getLocaleId(), userUpdateRequestDto.getLocaleId())) {
				return;
			}
			ServiceRegionLocaleMapping serviceRegionLocaleMapping = localeRepository.getLocaleById(
				userUpdateRequestDto.getLocaleId()).orElseThrow(() -> new CustomException(NOT_FOUND_LOCALE));

			ServiceRegion serviceRegion = regionRepository.getRegion(serviceRegionLocaleMapping.getServiceRegionId())
				.orElseThrow(() -> new CustomException(NOT_FOUND_REGION));

			currentUser.updateLocale(
				serviceRegionLocaleMapping.getId(),
				serviceRegionLocaleMapping.getCode(),
				serviceRegion.getId(),
				serviceRegion.getCode(),
				serviceRegion.getAwsCode()
			);
		}

		if (StringUtils.isNotBlank(userUpdateRequestDto.getMarketInfoReceive())) {
			AcceptOrReject acceptOrReject = AcceptOrReject.valueOf(userUpdateRequestDto.getMarketInfoReceive());
			if (currentUser.getMarketInfoReceive() == acceptOrReject) {
				return;
			}
			currentUser.updateMarketInfoReceive(acceptOrReject);
		}

		try {
			externalServiceMappingService.updateUser(currentUser);
		} catch (FeignException e) {
			StringWriter stackTraceContent = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTraceContent));
			log.error(stackTraceContent.toString());
		}
	}

	@Override
	@Transactional
	public void updateCurrentUserProfileImage(UserUpdateImageRequestDto userUpdateImageRequestDto) {
		Long userId = SecurityUtil.getCurrentUserId();
		User currentUser = userRepository.getJoinUserById(userId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		currentUser.setProfileImage(getUserProfileUrl(userUpdateImageRequestDto.getProfileImage(), userId));

		this.sendUpdateUserToWorkspaceAndSquarsAndNotification(currentUser);
	}

	@Override
	@Transactional
	public void deleteCurrentUserProfileImage() {
		Long userId = SecurityUtil.getCurrentUserId();
		User currentUser = userRepository.getJoinUserById(userId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		currentUser.setProfileImage(null);

		this.sendUpdateUserToWorkspaceAndSquarsAndNotification(currentUser);
	}

	@Override
	public User getUserByOrganizationId(Long organizationId) {
		return userRepository.getUserByOrganizationId(organizationId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}

	private void sendUpdateUserToWorkspaceAndSquarsAndNotification(User currentUser) {
		UserSendDto userSendDto = UserSendDto.from(currentUser);
		workspaceAPIService.syncUser(userSendDto);
		squarsAPIService.syncUser(userSendDto);
		notificationAPIService.syncUser(userSendDto);
	}

	@Transactional
	public String getUserProfileUrl(MultipartFile uploadProfileImage, Long userId) {
		if (uploadProfileImage == null) {
			return null;
		}
		return fileService.profileUpload(uploadProfileImage, userId);
	}

	@Override
	@Transactional
	public void setUserRole(Long userId, Role requestRole, UseStatus requestStatus) {
		Optional<UserRole> currentUserRole = userRoleRepository.getUserRoleByUserIdAndRole(userId, requestRole);
		currentUserRole.ifPresent(userRole -> {
			userRole.setUseRole(requestStatus);
			userRoleRepository.save(userRole);
		});

		if (currentUserRole.isEmpty()) {
			UserRole newUserRole = UserRole.userRoleBuilder()
				.role(requestRole)
				.userId(userId)
				.build();
			userRoleRepository.save(newUserRole);
		}
	}

	@Override
	public void passwordVerification(
		PasswordVerificationRequestDto passwordVerificationRequestDto
	) {
		String currentUserEmail = SecurityUtil.getCurrentUserEmail();

		CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(currentUserEmail);

		if (!passwordEncoder.matches(passwordVerificationRequestDto.getPassword(), customUserDetails.getPassword())) {
			throw new CustomException(INVALID_USER);
		}
	}

	@Transactional
	@Override
	public void updatePassword(
		UpdatePasswordRequestDto updatePasswordRequestDto
	) {
		emailAuthService.emailAuthCodeVerification(updatePasswordRequestDto);

		User user = userRepository.getUser(updatePasswordRequestDto.getEmail())
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		if (!updatePasswordRequestDto.getNewPassword().equals(updatePasswordRequestDto.getCheckPassword())) {
			throw new CustomException(INVALID_PASSWORD_MATCHING);
		}

		if (passwordEncoder.matches(updatePasswordRequestDto.getNewPassword(), user.getPassword())) {
			throw new CustomException(DUPLICATE_PASSWORD);
		}
		user.updatePassword(getEncodedPassword(updatePasswordRequestDto.getNewPassword()));

		redisRepository.deleteObjectValue(updatePasswordRequestDto.getEmail());
	}

	@Transactional
	@Override
	public void updateCurrentUserPassword(UserUpdatePasswordRequestDto updatePasswordRequestDto) {
		Long userId = SecurityUtil.getCurrentUserId();
		User currentUser = userRepository.getJoinUserById(userId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		if (!passwordEncoder.matches(updatePasswordRequestDto.getPassword(), currentUser.getPassword())) {
			throw new CustomException(INVALID_USER);
		}

		if (!updatePasswordRequestDto.getNewPassword().equals(updatePasswordRequestDto.getCheckPassword())) {
			throw new CustomException(INVALID_PASSWORD_MATCHING);
		}

		if (passwordEncoder.matches(updatePasswordRequestDto.getNewPassword(), currentUser.getPassword())) {
			throw new CustomException(DUPLICATE_PASSWORD);
		}
		currentUser.updatePassword(getEncodedPassword(updatePasswordRequestDto.getNewPassword()));
	}

	public String getEncodedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserRevisionResponseDto> getUserRevision(Long userId) {
		return userRevisionRepository.getUserRevisionResponses(userId);
	}

	@Override
	public void sync(Long userId) {
		User user = userRepository.getUser(userId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));
		this.sendSyncUserToWorkspaceAndSquarsAndNotification(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserStatisticsResponseDto getStatistics(UserStatisticsSearchDto userStatisticsSearchDto) {
		final DateRangeType requestDateRange = userStatisticsSearchDto.dateRangeTypeValueOf();
		ZonedDateTime startDate = requestDateRange.getStartDate();
		ZonedDateTime endDate = requestDateRange.getEndDate();

		if (requestDateRange.isCustom()) {
			startDate = userStatisticsSearchDto.getStartDate();
			endDate = userStatisticsSearchDto.getEndDate();
		}

		return userRepository.getStatistics(startDate, endDate);
	}

	@Override
	@Transactional(readOnly = true)
	public JoinedUserStatisticsResponseDto getStatisticsByJoinedUser() {
		return userRepository.getStatisticsByJoinedUser();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateLastLoginDate(String email) {
		try {
			User user = userRepository.getUser(email)
				.orElseThrow(() -> new CustomException(NOT_FOUND_USER));
			user.setLastLoginDate();
		} catch (Exception ignore) {
		}
	}

	private void sendSyncUserToWorkspaceAndSquarsAndNotification(User user) {
		UserSendDto userSendDto = UserSendDto.from(user);
		workspaceAPIService.syncUserByAdmin(userSendDto);
		squarsAPIService.syncUserByAdmin(userSendDto);
		notificationAPIService.syncUserByAdmin(userSendDto);
	}

}
