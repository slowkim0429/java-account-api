package com.virnect.account.security.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.config.message.localeMessageConverter;
import com.virnect.account.domain.model.AdminUser;
import com.virnect.account.domain.model.AdminUserRole;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.AdminUserRepository;
import com.virnect.account.port.outbound.AdminUserRoleRepository;

@Service
@RequiredArgsConstructor
public class CustomAdminUserDetailsService implements UserDetailsService {
	private final AdminUserRepository adminUserRepository;
	private final AdminUserRoleRepository adminUserRoleRepository;
	private final localeMessageConverter localeMessage;

	@Override
	@Transactional
	public CustomAdminUserDetails loadUserByUsername(String email) {
		AdminUser adminUser = adminUserRepository.getAdminUser(email)
			.orElseThrow(
				() -> new UsernameNotFoundException(localeMessage.getMessage(ErrorCode.INVALID_USER.toString())));

		if (!adminUser.getApprovalStatus().isApproved()) {
			throw new UsernameNotFoundException(localeMessage.getMessage(ErrorCode.INVALID_USER.toString()));
		}

		List<AdminUserRole> adminUserRoles = adminUserRoleRepository.getAdminUserRoles(adminUser.getId());
		return CustomAdminUserDetails.of(adminUser, adminUserRoles);
	}
}
