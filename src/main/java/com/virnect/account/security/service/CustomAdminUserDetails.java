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

import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.model.AdminUser;
import com.virnect.account.domain.model.AdminUserRole;

@Getter
@RequiredArgsConstructor
public class CustomAdminUserDetails implements UserDetails {
	private final Long id;
	private final String nickname;
	private final String email;
	private final String password;
	private final String profile;
	private final Color profileColor;
	private final Long localeId;
	private final String localeCode;
	private final Long regionId;
	private final String regionCode;
	private final String language;
	private final ZonedDateTime createdDate;
	private final ZonedDateTime updateDate;
	private final Collection<? extends GrantedAuthority> authorities;

	public static CustomAdminUserDetails of(AdminUser adminUser, List<AdminUserRole> adminUserRoles) {
		Collection<? extends GrantedAuthority> authorities = adminUserRoles.stream()
				.map(adminUserRole -> adminUserRole.getRole().name())
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return new CustomAdminUserDetails(
			adminUser.getId(),
			adminUser.getNickname(),
			adminUser.getEmail(),
			adminUser.getPassword(),
			adminUser.getProfileImage(),
			adminUser.getProfileColor(),
			adminUser.getLocaleId(),
			adminUser.getLocaleCode(),
			adminUser.getRegionId(),
			adminUser.getRegionCode(),
			adminUser.getLanguage(),
			adminUser.getCreatedDate(),
			adminUser.getUpdatedDate(),
			authorities
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
