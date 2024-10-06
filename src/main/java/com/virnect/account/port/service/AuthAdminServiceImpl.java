package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.TokenRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.AdminUserRequestDto;
import com.virnect.account.adapter.inbound.dto.request.validate.AdminUserLoginRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.converter.ValueConverter;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.model.AdminUser;
import com.virnect.account.domain.model.AdminUserRole;
import com.virnect.account.domain.model.ServiceRegion;
import com.virnect.account.domain.model.ServiceRegionLocaleMapping;
import com.virnect.account.exception.CustomException;
import com.virnect.account.log.NoLogging;
import com.virnect.account.port.inbound.AuthAdminService;
import com.virnect.account.port.inbound.EmailAuthAdminService;
import com.virnect.account.port.inbound.UserService;
import com.virnect.account.port.outbound.AdminUserRepository;
import com.virnect.account.port.outbound.AdminUserRoleRepository;
import com.virnect.account.port.outbound.LocaleRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.RegionRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.security.jwt.TokenProvider;
import com.virnect.account.security.service.CustomAdminUserDetails;
import com.virnect.account.security.service.CustomAdminUserDetailsService;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthAdminServiceImpl implements AuthAdminService {
	private final CustomAdminUserDetailsService customAdminUserDetailsService;
	private final EmailAuthAdminService emailAuthAdminService;
	private final UserService userService;

	private final RedisRepository redisRepository;
	private final LocaleRepository localeRepository;
	private final RegionRepository regionRepository;
	private final AdminUserRepository adminUserRepository;
	private final AdminUserRoleRepository adminUserRoleRepository;

	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;

	private static final String REGEXP_EMAIL_DOMAIN_OF_ADMIN = "(virnect.com|squars.io)";

	@Override
	public void signUp(AdminUserRequestDto adminUserRequestDto) {
		String requestEmail = adminUserRequestDto.getLowerCaseEmail();
		String emailDomain = ValueConverter.getEmailDomain(requestEmail);

		if (!emailDomain.matches(REGEXP_EMAIL_DOMAIN_OF_ADMIN)) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}

		checkEmailIsDuplicated(requestEmail);

		ServiceRegionLocaleMapping locale = localeRepository.getLocaleById(adminUserRequestDto.getLocaleId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LOCALE));

		ServiceRegion region = regionRepository.getRegion(locale.getServiceRegionId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_REGION));

		String encodedPassword = passwordEncoder.encode(adminUserRequestDto.getPassword());
		String initialNickname = userService.createInitialNickname(adminUserRequestDto.getLowerCaseEmail());

		AdminUser adminUser = AdminUser.of(adminUserRequestDto, initialNickname, encodedPassword, locale, region);
		adminUserRepository.save(adminUser);

		AdminUserRole adminUserRole = AdminUserRole.of(adminUser.getId(), Role.ROLE_ADMIN_USER);
		adminUserRoleRepository.save(adminUserRole);

		adminUserRepository.getAdminUsersByRole(Role.ROLE_ADMIN_MASTER)
			.forEach(
				adminMasterUser -> emailAuthAdminService.sendAdminAccountApplyEmail(
					adminUser.getRegionId(), adminUser.getEmail(), adminMasterUser.getEmail(), adminUser.getNickname())
			);
	}

	private void checkEmailIsDuplicated(String requestEmail) {
		adminUserRepository.getAdminUser(requestEmail.toLowerCase())
			.ifPresent(
				adminUser -> {
					if (adminUser.getApprovalStatus().isApproved()) {
						throw new CustomException(DUPLICATE_USER);
					}

					if (adminUser.getApprovalStatus().isRegister()) {
						throw new CustomException(DUPLICATE_APPLIED_USER);
					}
				}
			);
	}

	@NoLogging
	public TokenResponseDto signIn(AdminUserLoginRequestDto adminUserLoginRequestDto) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		CustomAdminUserDetails customAdminUserDetails = customAdminUserDetailsService.loadUserByUsername(adminUserLoginRequestDto.getEmail());

		if (!passwordEncoder.matches(adminUserLoginRequestDto.getPassword(), customAdminUserDetails.getPassword())) {
			throw new CustomException(INVALID_USER);
		}

		TokenResponseDto tokenDto = tokenProvider.createAdminToken(customAdminUserDetails);

		redisRepository.setAdminRefreshToken(
			customAdminUserDetails.getId().toString(), tokenDto.getRefreshToken(),
			tokenProvider.getRefreshTokenExpireTime()
		);

		return tokenDto;
	}

	@Override
	@NoLogging
	public void signOut() {
		Long adminId = SecurityUtil.getCurrentUserId();
		this.signOut(adminId);
	}

	@Override
	public void signOut(Long adminUserId) {
		redisRepository.deleteAdminRefreshToken(adminUserId.toString());
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

		CustomAdminUserDetails customAdminUserDetails = customAdminUserDetailsService.loadUserByUsername(
			tokenProvider.getUserEmailFromJwtToken(tokenRequestDto.getAccessToken()));

		TokenResponseDto tokenDto = tokenProvider.createAdminToken(customAdminUserDetails);

		redisRepository.setAdminRefreshToken(
			customAdminUserDetails.getId().toString(), tokenDto.getRefreshToken(),
			tokenProvider.getRefreshTokenExpireTime()
		);

		return tokenDto;
	}
}
