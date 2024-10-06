package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.account.adapter.inbound.dto.request.InviteUserAssignRequestDto;
import com.virnect.account.adapter.inbound.dto.request.LoginRequestDto;
import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.TokenRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.adapter.outbound.request.OrganizationLicenseSendDto;
import com.virnect.account.adapter.outbound.request.OrganizationSendDto;
import com.virnect.account.adapter.outbound.request.UserSendDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.domain.model.ServiceRegion;
import com.virnect.account.domain.model.ServiceRegionLocaleMapping;
import com.virnect.account.domain.model.ServiceTimeZone;
import com.virnect.account.domain.model.User;
import com.virnect.account.domain.model.UserRole;
import com.virnect.account.exception.CustomException;
import com.virnect.account.log.NoLogging;
import com.virnect.account.port.inbound.AuthService;
import com.virnect.account.port.inbound.ContractAPIService;
import com.virnect.account.port.inbound.EmailAuthService;
import com.virnect.account.port.inbound.ExternalServiceMappingService;
import com.virnect.account.port.inbound.InviteService;
import com.virnect.account.port.inbound.NotificationAPIService;
import com.virnect.account.port.inbound.OrganizationLicenseService;
import com.virnect.account.port.inbound.OrganizationService;
import com.virnect.account.port.inbound.SquarsApiService;
import com.virnect.account.port.inbound.UserService;
import com.virnect.account.port.inbound.WorkspaceAPIService;
import com.virnect.account.port.outbound.LocaleRepository;
import com.virnect.account.port.outbound.OrganizationRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.RegionRepository;
import com.virnect.account.port.outbound.ServiceTimeZoneRepository;
import com.virnect.account.port.outbound.UserRepository;
import com.virnect.account.port.outbound.UserRoleRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.security.jwt.TokenProvider;
import com.virnect.account.security.service.CustomUserDetails;
import com.virnect.account.security.service.CustomUserDetailsService;
import com.virnect.account.util.ZonedDateTimeUtil;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final InviteService inviteService;
	private final OrganizationLicenseService organizationLicenseService;
	private final OrganizationService organizationService;
	private final CustomUserDetailsService customUserDetailsService;
	private final EmailAuthService emailAuthService;
	private final UserService userService;
	private final ExternalServiceMappingService externalServiceMappingService;
	private final SquarsApiService squarsApiService;
	private final WorkspaceAPIService workspaceAPIService;
	private final ContractAPIService contractAPIService;
	private final NotificationAPIService notificationAPIService;

	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;
	private final RedisRepository redisRepository;
	private final LocaleRepository localeRepository;
	private final RegionRepository regionRepository;
	private final ServiceTimeZoneRepository serviceTimeZoneRepository;
	private final OrganizationRepository organizationRepository;
	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final TaskExecutor asyncFeignTaskExecutor;

	@Override
	public void signup(UserCreateRequestDto userCreateRequestDto) {
		String requestEmail = userCreateRequestDto.getLowerCaseEmail();

		emailAuthCodeValidation(requestEmail, userCreateRequestDto.getAuthCode());

		if (StringUtils.isNotBlank(userCreateRequestDto.getInviteToken())) {
			inviteTokenValidation(requestEmail, userCreateRequestDto.getInviteToken());
		}

		if (userRepository.existsUserByEmail(requestEmail)) {
			throw new CustomException(DUPLICATE_USER);
		}

		ServiceRegionLocaleMapping locale = localeRepository.getLocale(userCreateRequestDto.getLocaleCode())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LOCALE));

		ServiceRegion region = regionRepository.getRegion(locale.getServiceRegionId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_REGION));

		ServiceTimeZone serviceTimeZone = serviceTimeZoneRepository.getServiceTimeZone(
				userCreateRequestDto.getLocaleCode())
			.orElseThrow(() -> new CustomException(NOT_FOUND_SERVICE_TIME_ZONE));

		String encodedPassword = passwordEncoder.encode(userCreateRequestDto.getPassword());
		String initialNickname = userService.createInitialNickname(requestEmail);

		User newUser = User.signUpBuilder()
			.requestDto(userCreateRequestDto)
			.nickname(initialNickname)
			.encodedPassword(encodedPassword)
			.userLocale(locale)
			.userRegion(region)
			.zoneId(serviceTimeZone.getZoneId())
			.build();

		userRepository.save(newUser);

		userService.setUserRole(newUser.getId(), Role.ROLE_USER, UseStatus.USE);

		Organization newOrganization = organizationService.create(newUser, locale);

		TokenResponseDto tokenResponseDto = tokenProvider.createToken(newUser.getEmail());
		String authorizationHeaderValue = tokenProvider.createAuthorizationHeaderValue(tokenResponseDto);

		UserSendDto userSendDto = UserSendDto.from(newUser);
		
		workspaceAPIService.syncUserByNonUser(userSendDto, authorizationHeaderValue);
		squarsApiService.syncUserByNonUser(userSendDto, authorizationHeaderValue);
		notificationAPIService.syncUserByNonUser(userSendDto, authorizationHeaderValue);

		OrganizationSendDto organizationSendDto = OrganizationSendDto.from(newOrganization);

		contractAPIService.syncOrganizationByNonUser(
			newOrganization.getId(), organizationSendDto, authorizationHeaderValue);

		OrganizationLicenseSendDto organizationLicenseSendDto = organizationLicenseService.provideFreePlusOrganizationLicenseAtFirst(
			newUser.getOrganizationId(), ZonedDateTimeUtil.zoneOffsetOfUTC());

		workspaceAPIService.syncOrganizationLicense(organizationLicenseSendDto, authorizationHeaderValue);

		squarsApiService.syncOrganizationLicense(organizationLicenseSendDto, authorizationHeaderValue);

		if (StringUtils.isNotBlank(userCreateRequestDto.getInviteToken())) {
			inviteService.assignUser(new InviteUserAssignRequestDto(userCreateRequestDto.getInviteToken()));
		}

		externalServiceMappingService.register(newUser, newOrganization);

		redisRepository.deleteObjectValue(requestEmail);

		emailAuthService.sendSignupWelcomeEmail(newUser);
	}

	private void inviteTokenValidation(String requestEmail, String inviteToken) {
		String inviteEmail = tokenProvider.getUserEmailFromJwtToken(inviteToken);
		if (!requestEmail.equals(inviteEmail)) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}

		Long invitedOrganizationId = tokenProvider.getOrganizationIdFromJwtToken(inviteToken);

		if (organizationRepository.getOrganization(invitedOrganizationId).isEmpty()) {
			throw new CustomException(NOT_FOUND_ORGANIZATION);
		}
	}

	@NoLogging
	@Transactional(readOnly = true)
	public TokenResponseDto signIn(LoginRequestDto loginDto) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(loginDto.getEmail());

		if (!passwordEncoder.matches(loginDto.getPassword(), customUserDetails.getPassword())) {
			throw new CustomException(INVALID_USER);
		}

		TokenResponseDto tokenDto = tokenProvider.createToken(customUserDetails);

		redisRepository.setObjectValue(
			customUserDetails.getId().toString(), tokenDto.getRefreshToken(),
			tokenProvider.getRefreshTokenExpireTime()
		);

		externalServiceMappingService.updateLoginDate(customUserDetails.getHubspotContactId(), LocalDate.now());
		userService.updateLastLoginDate(loginDto.getEmail());

		return tokenDto;
	}

	@Override
	@NoLogging
	public void signOut() {
		Long userId = SecurityUtil.getCurrentUserId();
		redisRepository.deleteObjectValue(userId.toString());
	}

	@NoLogging
	public TokenResponseDto reissue(TokenRequestDto tokenRequestDto) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
			throw new CustomException(INVALID_TOKEN);
		}

		String userId = tokenProvider.getUserNameFromJwtToken(tokenRequestDto.getAccessToken());

		String signedRefreshToken = redisRepository.getStringValue(userId);

		if (StringUtils.isBlank(signedRefreshToken)) {
			throw new CustomException(INVALID_TOKEN);
		}

		if (!signedRefreshToken.equals(tokenRequestDto.getRefreshToken())) {
			throw new CustomException(INVALID_TOKEN);
		}

		CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(
			tokenProvider.getUserEmailFromJwtToken(tokenRequestDto.getAccessToken()));

		TokenResponseDto tokenDto = tokenProvider.createToken(customUserDetails, tokenRequestDto.getRefreshToken());

		return tokenDto;
	}

	public void emailAuthCodeValidation(String email, String userChallengeAuthCode) {
		String verifiedAuthCode = redisRepository.getStringValue(email);

		if (verifiedAuthCode == null) {
			throw new CustomException(EXPIRED_EMAIL_SESSION_CODE);
		}

		if (!verifiedAuthCode.equals(userChallengeAuthCode)) {
			throw new CustomException(INVALID_EMAIL_AUTH_CODE);
		}
	}

	@Transactional
	public void resign(PasswordVerificationRequestDto passwordVerificationRequestDto) {
		Long userId = SecurityUtil.getCurrentUserId();
		User currentUser = userRepository.getJoinUserById(userId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		if (!passwordEncoder.matches(passwordVerificationRequestDto.getPassword(), currentUser.getPassword())) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}

		Long organizationId = SecurityUtil.getCurrentUserOrganizationId();

		Organization organization = organizationRepository.getOrganization(organizationId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION));

		String email = SecurityUtil.getCurrentUserEmail();
		String nickname = SecurityUtil.getCurrentUserNickname();

		List<UserRole> userRoles = userRoleRepository.getUserUseRoles(userId);
		for (UserRole userRole : userRoles) {
			userRole.setUseRole(UseStatus.DELETE);
		}

		currentUser.delete();
		organization.delete();

		OrganizationSendDto organizationSendDto = OrganizationSendDto.from(organization);

		emailAuthService.sendUserResignEmail(nickname, email);

		UserSendDto userSendDto = UserSendDto.from(currentUser);

		CompletableFuture<?> syncOrganizationToContract = contractAPIService.syncOrganizationCompletableFuture(
			organization.getId(), organizationSendDto);
		CompletableFuture<?> syncUserToWorkspace = workspaceAPIService.syncUserCompletableFuture(userSendDto);
		CompletableFuture<?> syncUserToStudio = squarsApiService.syncUserCompletableFuture(userSendDto);
		CompletableFuture<?> syncUserToNotification = notificationAPIService.syncUserCompletableFuture(userSendDto);

		try {
			externalServiceMappingService.updateUser(currentUser);
		} catch (FeignException ignore) {
			StringWriter stackTraceContent = new StringWriter();
			ignore.printStackTrace(new PrintWriter(stackTraceContent));
			log.error(stackTraceContent.toString());
		}

		try {
			CompletableFuture.allOf(
				syncOrganizationToContract, syncUserToWorkspace, syncUserToStudio, syncUserToNotification).get();
		} catch (Exception ignore) {
			StringWriter stackTraceContent = new StringWriter();
			ignore.printStackTrace(new PrintWriter(stackTraceContent));
			log.error(stackTraceContent.toString());
		}
		signOut();
	}
}
