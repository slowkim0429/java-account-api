package com.virnect.account.security;

import static com.virnect.account.exception.ErrorCode.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.virnect.account.exception.CustomException;
import com.virnect.account.security.service.CustomAdminUserDetails;
import com.virnect.account.security.service.CustomTrackUserDetails;
import com.virnect.account.security.service.CustomUserDetails;
import com.virnect.account.util.ZonedDateTimeUtil;

public class SecurityUtil {

	private SecurityUtil() {
	}

	public static Long getCurrentUserId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		Long userId = 0L;

		if (authentication.getPrincipal() instanceof CustomUserDetails ||
			authentication.getPrincipal() instanceof CustomAdminUserDetails) {
			userId = Long.parseLong(authentication.getName());
		}

		return userId;
	}

	public static String getCurrentUserNickname() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		String nickname = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
			nickname = ((CustomUserDetails)springSecurityUser).getNickname();
		}

		return nickname;
	}

	public static String getCurrentUserEmail() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		String email = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
			email = ((CustomUserDetails)springSecurityUser).getEmail();
		}

		return email;
	}

	public static String getCurrentUserLanguage() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		String language = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
			language = ((CustomUserDetails)springSecurityUser).getLanguage();
		}

		return language;
	}

	public static Long getCurrentUserOrganizationId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		Long organizationId = 0l;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
			organizationId = ((CustomUserDetails)springSecurityUser).getOrganizationId();
		}

		return organizationId;
	}

	public static String getCurrentUserLocaleCode() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}
		if (authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
			return customUserDetails.getLocaleCode();
		}

		return null;
	}

	public static String getCurrentUserRegionCode() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
			return ((CustomUserDetails)springSecurityUser).getRegionCode();
		}

		return null;
	}

	public static Long getCurrentUserRegionId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
			return ((CustomUserDetails)springSecurityUser).getRegionId();
		}

		return null;
	}

	public static String getCurrentAdminUserEmail() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		String email = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
			email = ((CustomAdminUserDetails)springSecurityUser).getEmail();
		}

		return email;
	}

	public static Long getCurrentAdminUserId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		long adminUserId = 0L;

		if (authentication.getPrincipal() instanceof CustomAdminUserDetails) {
			adminUserId = Long.parseLong(authentication.getName());
		}

		return adminUserId;
	}

	public static Long getCurrentOrganizationLicenseKeyId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		Long organizationLicenseKeyId = 0L;

		if (authentication.getPrincipal() instanceof CustomTrackUserDetails) {
			UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
			organizationLicenseKeyId = ((CustomTrackUserDetails)springSecurityUser).getOrganizationLicenseKeyId();
		}

		return organizationLicenseKeyId;
	}

	public static String getCurrentZoneId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getName() == null) {
			throw new CustomException(INVALID_AUTHENTICATION);
		}

		String zoneId = ZonedDateTimeUtil.DEFAULT_ZONE;

		if (authentication.getPrincipal() instanceof CustomUserDetails) {
			UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
			zoneId = ((CustomUserDetails)springSecurityUser).getZoneId();
		}

		return zoneId;
	}

	public static boolean isAuthenticationUser() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.getName() != null;
	}
}
