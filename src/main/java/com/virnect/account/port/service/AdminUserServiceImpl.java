package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.account.adapter.inbound.dto.request.AuthCodeVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.adminuser.AdminUserAuthorityGroupRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.AdminUserSearchDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.response.AdminUserResponseDto;
import com.virnect.account.adapter.inbound.dto.response.AdminUserRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.model.AdminUser;
import com.virnect.account.domain.model.AdminUserRole;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.AdminUserService;
import com.virnect.account.port.inbound.AuthAdminService;
import com.virnect.account.port.inbound.AuthorityGroupService;
import com.virnect.account.port.inbound.EmailAuthAdminService;
import com.virnect.account.port.outbound.AdminUserRepository;
import com.virnect.account.port.outbound.AdminUserRevisionRepository;
import com.virnect.account.port.outbound.AdminUserRoleRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.util.ZonedDateTimeUtil;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {
	private static final String ADMIN_DOMAIN_RECORD_NAME = "ADMIN";
	private final AdminUserRepository adminUserRepository;
	private final RedisRepository redisRepository;
	private final AdminUserRoleRepository adminUserRoleRepository;
	private final AdminUserRevisionRepository adminUserRevisionRepository;
	private final AuthorityGroupService authorityGroupService;
	private final EmailAuthAdminService emailAuthAdminService;
	private final AuthAdminService authAdminService;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<AdminUserResponseDto> getAdminUsers(
		AdminUserSearchDto adminUserSearchDto, Pageable pageable
	) {
		Page<AdminUserResponseDto> adminUserResponseDtos = adminUserRepository.getAdminUserResponses(
			adminUserSearchDto, pageable);
		return new PageContentResponseDto<>(adminUserResponseDtos, pageable);
	}

	@Override
	public void updateApprovalStatus(Long adminUserId, ApprovalStatus status) {
		AdminUser adminUser = adminUserRepository.findById(adminUserId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		if (MembershipStatus.RESIGN.equals(adminUser.getStatus())) {
			throw new CustomException(NOT_FOUND_ADMIN_USER);
		}

		if (adminUser.getApprovalStatus().isImmutableStatus()) {
			throw new CustomException(INVALID_STATUS);
		}

		adminUser.setApprovalStatus(status);
		adminUserRepository.save(adminUser);

		if (status.isApproved()) {
			emailAuthAdminService.sendAdminAccountApprovedEmail(
				adminUser.getRegionId(), adminUser.getEmail(), adminUser.getNickname(),
				ZonedDateTimeUtil.zoneOffsetOfUTC()
			);
		}
	}

	@Override
	public AdminUserResponseDto getCurrentAdminUser() {
		Long currentAdminUserId = SecurityUtil.getCurrentAdminUserId();

		return adminUserRepository.getAdminUserResponse(currentAdminUserId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ADMIN_USER));
	}

	@Override
	@Transactional(readOnly = true)
	public AdminUserResponseDto getAdminUserResponse(Long adminUserId) {
		return adminUserRepository.getAdminUserResponse(adminUserId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ADMIN_USER));
	}

	@Override
	public void updatePasswordWithoutAuthentication(UpdatePasswordRequestDto updatePasswordRequestDto) {
		if (!updatePasswordRequestDto.getNewPassword().equals(updatePasswordRequestDto.getCheckPassword())) {
			throw new CustomException(INVALID_PASSWORD_MATCHING);
		}

		AuthCodeVerificationRequestDto authCodeVerificationRequestDto = new AuthCodeVerificationRequestDto();
		authCodeVerificationRequestDto.setEmail(updatePasswordRequestDto.getEmail());
		authCodeVerificationRequestDto.setCode(updatePasswordRequestDto.getCode());

		emailAuthAdminService.verifyResetPasswordEmailAuthCode(authCodeVerificationRequestDto);

		AdminUser adminUser = getAdminUser(updatePasswordRequestDto.getEmail());

		if (passwordEncoder.matches(updatePasswordRequestDto.getNewPassword(), adminUser.getPassword())) {
			throw new CustomException(DUPLICATE_PASSWORD);
		}
		adminUser.updatePassword(getEncodedPassword(updatePasswordRequestDto.getNewPassword()));

		redisRepository.deleteObjectValue(ADMIN_DOMAIN_RECORD_NAME + updatePasswordRequestDto.getEmail());
	}

	@Override
	public void updatePassword(UserUpdatePasswordRequestDto updatePasswordRequestDto) {
		if (!updatePasswordRequestDto.getNewPassword().equals(updatePasswordRequestDto.getCheckPassword())) {
			throw new CustomException(INVALID_PASSWORD_MATCHING);
		}

		String email = SecurityUtil.getCurrentAdminUserEmail();
		AdminUser adminUser = getAdminUser(email);

		if (!passwordEncoder.matches(updatePasswordRequestDto.getPassword(), adminUser.getPassword())) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}

		if (passwordEncoder.matches(updatePasswordRequestDto.getNewPassword(), adminUser.getPassword())) {
			throw new CustomException(DUPLICATE_PASSWORD);
		}
		adminUser.updatePassword(getEncodedPassword(updatePasswordRequestDto.getNewPassword()));
	}

	@Override
	public void resign(PasswordVerificationRequestDto passwordVerificationRequestDto) {
		String email = SecurityUtil.getCurrentAdminUserEmail();
		AdminUser adminUser = getAdminUser(email);

		if (!passwordEncoder.matches(passwordVerificationRequestDto.getPassword(), adminUser.getPassword())) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}

		List<AdminUserRole> adminUserRoles = adminUserRoleRepository.getAdminUserRoles(adminUser.getId());
		for (AdminUserRole adminUserRole : adminUserRoles) {
			adminUserRole.delete();
		}
		adminUser.delete();

		authAdminService.signOut();
	}

	@Override
	public void resignByAdminMaster(Long requestAdminUserId) {
		if (isAdminMaster(requestAdminUserId)) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}

		AdminUser adminUser = getAdminUser(requestAdminUserId);

		if (!MembershipStatus.JOIN.equals(adminUser.getStatus())) {
			throw new CustomException(NOT_FOUND_ADMIN_USER);
		}

		adminUser.delete();

		List<AdminUserRole> adminUserRoles = adminUserRoleRepository.getAdminUserRoles(adminUser.getId());
		for (AdminUserRole adminUserRole : adminUserRoles) {
			adminUserRole.delete();
		}

		authAdminService.signOut(requestAdminUserId);
	}

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<AdminUserRevisionResponseDto> getAdminUserRevisions(
		Long adminUserId, Pageable pageable
	) {
		Page<AdminUserRevisionResponseDto> adminUserRevisions = adminUserRevisionRepository.getAdminUserRevisionResponses(
			adminUserId, pageable);
		return new PageContentResponseDto<>(adminUserRevisions, pageable);
	}

	private boolean isAdminMaster(Long adminUserId) {
		List<AdminUserRole> adminUserRoles = adminUserRoleRepository.getAdminUserRoles(adminUserId);
		return adminUserRoles.stream()
			.anyMatch(adminUserRole -> Role.ROLE_ADMIN_MASTER.equals(adminUserRole.getRole()));
	}

	private String getEncodedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	@Override
	public void updateAuthorityGroup(
		Long adminUserid, AdminUserAuthorityGroupRequestDto adminUserAuthorityGroupRequestDto
	) {
		AdminUser adminUser = getAdminUser(adminUserid);
		if (!adminUser.isAvailableStatus()) {
			throw new CustomException(INVALID_ADMIN_USER_STATUS);
		}
		Long authorityGroupId = adminUserAuthorityGroupRequestDto.getAuthorityGroupId();
		authorityGroupService.validUseAuthorityGroup(authorityGroupId);
		adminUser.setAuthorityGroupId(authorityGroupId);
	}

	private AdminUser getAdminUser(Long adminUserId) {
		return adminUserRepository.getAdminUser(adminUserId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ADMIN_USER));
	}

	private AdminUser getAdminUser(String email) {
		return adminUserRepository.getAdminUser(email)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ADMIN_USER));
	}
}
