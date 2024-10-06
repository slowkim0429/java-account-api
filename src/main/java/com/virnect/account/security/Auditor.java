package com.virnect.account.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Auditor implements AuditorAware<Long> {
	@Override
	public Optional<Long> getCurrentAuditor()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (null == authentication || !authentication.isAuthenticated())
		{
			return Optional.of(0L);
		}

		return Optional.of(SecurityUtil.getCurrentUserId());
	}
}
