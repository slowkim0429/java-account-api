package com.virnect.account.security.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import lombok.Getter;

import com.virnect.account.security.jwt.TokenProvider;

@Getter
public class CustomTrackUserDetails implements UserDetails {
	private final Collection<? extends GrantedAuthority> authorities;
	private Long id;
	private Long organizationLicenseKeyId;

	public CustomTrackUserDetails(
		Long subject, Long organizationLicenseKeyId, Collection<? extends GrantedAuthority> authorities
	) {
		this.id = subject;
		this.organizationLicenseKeyId = organizationLicenseKeyId;
		this.authorities = authorities;
	}

	public static CustomTrackUserDetails from(Claims claims) {
		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(TokenProvider.AUTHORITIES_KEY).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return new CustomTrackUserDetails(
			Long.parseLong(claims.getSubject()),
			Long.parseLong(claims.get("organizationLicenseKeyId").toString()),
			authorities
		);
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public String getUsername() {
		return id.toString();
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
