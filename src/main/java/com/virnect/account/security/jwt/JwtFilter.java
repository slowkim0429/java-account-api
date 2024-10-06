package com.virnect.account.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.service.CustomAdminUserDetails;
import com.virnect.account.security.service.CustomAdminUserDetailsService;
import com.virnect.account.security.service.CustomTrackUserDetails;
import com.virnect.account.security.service.CustomTrackUserDetailsService;
import com.virnect.account.security.service.CustomUserDetails;
import com.virnect.account.security.service.CustomUserDetailsService;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private final TokenProvider tokenProvider;
	private final CustomUserDetailsService customUserDetailsService;
	private final CustomAdminUserDetailsService customAdminUserDetailsService;
	private final CustomTrackUserDetailsService customTrackUserDetailsService;
	private final RedisRepository redisRepository;

	@Override
	public void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws IOException, ServletException {
		String jwtToken = tokenProvider.getTokenFromHttpServletRequest(request);

		if (StringUtils.hasText(jwtToken) && tokenProvider.validateToken(jwtToken)) {
			String issuer = tokenProvider.getIssuerFromJwtToken(jwtToken);

			if (TokenProvider.ISS.equals(issuer)) {
				doFilterForUser(jwtToken);
			}

			if (TokenProvider.ADMIN_ISS.equals(issuer)) {
				doFilterForAdmin(jwtToken);
			}

			if (TokenProvider.TRACK_ISS.equals(issuer)) {
				doFilterForTrack(jwtToken);
			}

		} else {
			log.info("유효한 JWT 토큰이 없습니다.");
		}
		filterChain.doFilter(request, response);
	}

	private void doFilterForAdmin(String jwtToken) {
		String adminId = tokenProvider.getUserNameFromJwtToken(jwtToken);
		String signedRefreshToken = redisRepository.getAdminRefreshToken(adminId);

		if (!StringUtils.hasText(signedRefreshToken)) {
			log.info("로그아웃한 어드민 사용자 Admin({})", adminId);
		} else {
			CustomAdminUserDetails customAdminUserDetails = customAdminUserDetailsService.loadUserByUsername(
				tokenProvider.getUserEmailFromJwtToken(jwtToken));

			Authentication authentication = tokenProvider.getAdminAuthentication(customAdminUserDetails);

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	}

	private void doFilterForUser(String jwtToken) {
		String userId = tokenProvider.getUserNameFromJwtToken(jwtToken);
		String signedRefreshToken = redisRepository.getStringValue(userId);

		if (!StringUtils.hasText(signedRefreshToken)) {
			log.info("로그아웃한 사용자 User({})", userId);
		} else {
			CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(
				tokenProvider.getUserEmailFromJwtToken(jwtToken));

			Authentication authentication = tokenProvider.getAuthentication(customUserDetails);

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	}

	private void doFilterForTrack(String jwtToken) {

		CustomTrackUserDetails customTrackUserDetails = customTrackUserDetailsService.loadUserByUsername(jwtToken);

		Authentication authentication = tokenProvider.getTrackAuthentication(customTrackUserDetails);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
