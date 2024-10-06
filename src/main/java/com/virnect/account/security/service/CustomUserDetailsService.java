package com.virnect.account.security.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.config.message.localeMessageConverter;
import com.virnect.account.domain.model.User;
import com.virnect.account.domain.model.UserRole;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.AuthRepository;
import com.virnect.account.port.outbound.UserRoleRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final AuthRepository authRepository;
	private final UserRoleRepository userRoleRepository;
	private final localeMessageConverter localeMessage;

	@Override
	@Transactional
	public CustomUserDetails loadUserByUsername(String email) {
		User user = authRepository.getUserBySignIn(email)
			.orElseThrow(
				() -> new UsernameNotFoundException(localeMessage.getMessage(ErrorCode.INVALID_USER.toString())));

		List<UserRole> userRoles = userRoleRepository.getUserUseRoles(user.getId());
		return CustomUserDetails.of(user, userRoles);
	}
}
