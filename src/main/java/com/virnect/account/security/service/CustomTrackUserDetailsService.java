package com.virnect.account.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.OrganizationLicenseKeyRepository;
import com.virnect.account.security.jwt.TokenProvider;

@Service
@RequiredArgsConstructor
public class CustomTrackUserDetailsService implements UserDetailsService {

	private final TokenProvider tokenProvider;
	private final OrganizationLicenseKeyRepository organizationLicenseKeyRepository;

	@Override
	public CustomTrackUserDetails loadUserByUsername(String jwtToken) {
		Claims claims = tokenProvider.parseClaims(jwtToken);

		Long organizationLicenseKeyId = Long.parseLong(claims.get("organizationLicenseKeyId").toString());

		organizationLicenseKeyRepository.getOrganizationLicenseKey(null, organizationLicenseKeyId, UseStatus.USE)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_USER));

		return CustomTrackUserDetails.from(claims);
	}
}
