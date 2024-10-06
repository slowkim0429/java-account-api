package com.virnect.account.security.service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.AcceptOrReject;
import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.User;
import com.virnect.account.domain.model.UserRole;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
	private final Long id;
	private final String nickname;
	private final String email;
	private final String password;
	private final String profile;
	private final Color profileColor;
	private final AcceptOrReject marketInfoReceive;
	private final Long localeId;
	private final String localeCode;
	private final Long regionId;
	private final String regionCode;
	private final String language;
	private final Long organizationId;
	private final UseStatus organizationStatus;
	private final Long hubspotContactId;
	private final ZonedDateTime createdDate;
	private final ZonedDateTime updateDate;
	private final Collection<? extends GrantedAuthority> authorities;
	private final String zoneId;

	public static CustomUserDetails of(User user, List<UserRole> userRoles) {
		Collection<? extends GrantedAuthority> authorities =
			userRoles.stream()
				.map(userRole -> userRole.getRole().name())
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return new CustomUserDetails(
			user.getId(),
			user.getNickname(),
			user.getEmail(),
			user.getPassword(),
			user.getProfileImage(),
			user.getProfileColor(),
			user.getMarketInfoReceive(),
			user.getLocaleId(),
			user.getLocaleCode(),
			user.getRegionId(),
			user.getRegionCode(),
			user.getLanguage(),
			user.getOrganizationId(),
			user.getOrganizationStatus(),
			user.getHubSpotContactId(),
			user.getCreatedDate(),
			user.getUpdatedDate(),
			authorities,
			user.getZoneId()
		);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
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
