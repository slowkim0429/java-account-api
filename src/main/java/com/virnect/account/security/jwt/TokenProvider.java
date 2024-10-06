package com.virnect.account.security.jwt;

import static com.virnect.account.exception.ErrorCode.*;

import java.io.Serializable;
import java.security.Key;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.InviteRole;
import com.virnect.account.domain.enumclass.InviteType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.TrackRole;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.exception.CustomException;
import com.virnect.account.log.NoLogging;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.service.CustomAdminUserDetails;
import com.virnect.account.security.service.CustomTrackUserDetails;
import com.virnect.account.security.service.CustomUserDetails;
import com.virnect.account.security.service.CustomUserDetailsService;

@Slf4j
@Component
public class TokenProvider implements Serializable {
	public static final String ISS = "VIRNECT";
	public static final String ADMIN_ISS = "ADMIN";
	public static final String TRACK_ISS = "TRACK";
	public static final String AUTHORITIES_KEY = "roles";
	private static final String GRANT_TYPE = "Bearer";
	private static final String GRANT_TYPE_PREFIX = "Bearer ";
	private final CustomUserDetailsService customUserDetailsService;
	private final RedisRepository redisRepository;
	private final Key key;
	@Value("${security.jwt-config.access-token-expire}")
	private long accessTokenExpireTime = 0;
	@Value("${security.jwt-config.refresh-token-expire}")
	private long refreshTokenExpireTime = 0;
	@Value("${security.jwt-config.invite-token-expire}")
	private long inviteTokenExpireTime = 0;

	@Value("${security.jwt-config.tract-token-expire}")
	private long tractTokenExpireTime = 0;

	public TokenProvider(
		CustomUserDetailsService customUserDetailsService, RedisRepository redisRepository,
		@Value("${security.jwt-config.secret}") String secret
	) {
		this.customUserDetailsService = customUserDetailsService;
		this.redisRepository = redisRepository;
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public long getRefreshTokenExpireTime() {
		return refreshTokenExpireTime;
	}

	@NoLogging
	public TokenResponseDto createToken(CustomUserDetails customUserDetails) {
		return this.createToken(customUserDetails, null);
	}

	@NoLogging
	public TokenResponseDto createToken(CustomUserDetails customUserDetails, String refreshToken) {
		String authorities = customUserDetails.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		Date now = new Date();
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS512");

		Date accessTokenExpiresIn = new Date(now.getTime() + accessTokenExpireTime);
		String accessToken = Jwts.builder()
			.setHeader(headers)
			.setSubject(customUserDetails.getId().toString())
			.setIssuer(ISS)
			.setIssuedAt(now)
			.claim(AUTHORITIES_KEY, authorities)
			.claim("nickname", customUserDetails.getNickname() == null ? "nothing" : customUserDetails.getNickname())
			.claim("email", customUserDetails.getEmail())
			.claim("localeCode", customUserDetails.getLocaleCode())
			.claim("regionCode", customUserDetails.getRegionCode())
			.claim("language", customUserDetails.getLanguage())
			.claim("zoneId", customUserDetails.getZoneId())
			.claim(
				"organizationId",
				UseStatus.USE.equals(customUserDetails.getOrganizationStatus()) ?
					customUserDetails.getOrganizationId() : 0
			)
			.setExpiration(accessTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		Date refreshTokenExpiresIn = new Date(now.getTime() + refreshTokenExpireTime);

		if (StringUtils.isEmpty(refreshToken)) {
			refreshToken = Jwts.builder()
				.setHeader(headers)
				.setExpiration(refreshTokenExpiresIn)
				.signWith(key, SignatureAlgorithm.HS512)
				.compact();
		}

		return TokenResponseDto.builder()
			.grantType(GRANT_TYPE)
			.accessToken(accessToken)
			.accessTokenExpiresIn(accessTokenExpiresIn.getTime())
			.refreshToken(refreshToken)
			.build();
	}

	@NoLogging
	public TokenResponseDto createAdminToken(CustomAdminUserDetails customAdminUserDetails) {
		String authorities = customAdminUserDetails.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		Date now = new Date();
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS512");

		Date accessTokenExpiresIn = new Date(now.getTime() + accessTokenExpireTime);
		String accessToken = Jwts.builder()
			.setHeader(headers)
			.setSubject(customAdminUserDetails.getId().toString())
			.setIssuer(ADMIN_ISS)
			.setIssuedAt(now)
			.claim(AUTHORITIES_KEY, authorities)
			.claim("nickname", customAdminUserDetails.getNickname())
			.claim("email", customAdminUserDetails.getEmail())
			.claim("localeCode", customAdminUserDetails.getLocaleCode())
			.claim("regionCode", customAdminUserDetails.getRegionCode())
			.claim("language", customAdminUserDetails.getLanguage())
			.setExpiration(accessTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		Date refreshTokenExpiresIn = new Date(now.getTime() + refreshTokenExpireTime);
		String refreshToken = Jwts.builder()
			.setHeader(headers)
			.setExpiration(refreshTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		return TokenResponseDto.builder()
			.grantType(GRANT_TYPE)
			.accessToken(accessToken)
			.accessTokenExpiresIn(accessTokenExpiresIn.getTime())
			.refreshToken(refreshToken)
			.build();
	}

	public TokenResponseDto createToken(
		Long userId, Long organizationId, String nickname, String email, String language, List<Role> roles
	) {
		return createToken(userId, organizationId, nickname, email, language, roles, null);
	}

	public TokenResponseDto createToken(
		Long userId, Long organizationId, String nickname, String email, String language, List<Role> roles,
		Long refreshTokenExpireTime
	) {
		if (refreshTokenExpireTime == null) {
			refreshTokenExpireTime = this.refreshTokenExpireTime;
		}
		String authorities = roles.stream()
			.map(role -> String.valueOf(role))
			.collect(Collectors.joining(","));

		Date now = new Date();
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS512");

		Date accessTokenExpiresIn = new Date(now.getTime() + accessTokenExpireTime);
		String accessToken = Jwts.builder()
			.setHeader(headers)
			.setSubject(userId.toString())
			.setIssuer(ISS)
			.setIssuedAt(now)
			.claim(AUTHORITIES_KEY, authorities)
			.claim("nickname", nickname)
			.claim("email", email)
			.claim("language", language)
			.claim("organizationId", organizationId)
			.setExpiration(accessTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		Date refreshTokenExpiresIn = new Date(now.getTime() + refreshTokenExpireTime);
		String refreshToken = Jwts.builder()
			.setHeader(headers)
			.setExpiration(refreshTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		return TokenResponseDto.builder()
			.grantType(GRANT_TYPE)
			.accessToken(accessToken)
			.accessTokenExpiresIn(accessTokenExpiresIn.getTime())
			.refreshToken(refreshToken)
			.build();
	}

	public TokenResponseDto createAdminToken(
		Long adminUserId, String email, String language, List<Role> roles
	) {
		String authorities = roles.stream()
			.map(role -> String.valueOf(role))
			.collect(Collectors.joining(","));

		Date now = new Date();
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS512");

		Date accessTokenExpiresIn = new Date(now.getTime() + accessTokenExpireTime);
		String accessToken = Jwts.builder()
			.setHeader(headers)
			.setSubject(adminUserId.toString())
			.setIssuer(ADMIN_ISS)
			.setIssuedAt(now)
			.claim(AUTHORITIES_KEY, authorities)
			.claim("email", email)
			.claim("language", language)
			.setExpiration(accessTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		Date refreshTokenExpiresIn = new Date(now.getTime() + refreshTokenExpireTime);
		String refreshToken = Jwts.builder()
			.setHeader(headers)
			.setExpiration(refreshTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		return TokenResponseDto.builder()
			.grantType(GRANT_TYPE)
			.accessToken(accessToken)
			.accessTokenExpiresIn(accessTokenExpiresIn.getTime())
			.refreshToken(refreshToken)
			.build();
	}

	public TokenResponseDto createToken(String email) {
		CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(email);

		String userRefreshToken = redisRepository.getStringValue(customUserDetails.getId().toString());

		if (org.apache.commons.lang3.StringUtils.isNotBlank(userRefreshToken)) {
			return createToken(customUserDetails, userRefreshToken);
		}

		TokenResponseDto tokenDto = createToken(customUserDetails);
		redisRepository.setObjectValue(
			customUserDetails.getId().toString(), tokenDto.getRefreshToken(),
			getRefreshTokenExpireTime()
		);

		return tokenDto;
	}

	@NoLogging
	public String createInviteTicket(
		String email, Long userId, Long workspaceId, Long groupId,
		InviteType inviteType, InviteRole role
	) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS512");

		Date now = new Date();
		Date inviteTokenExpiresIn = new Date(now.getTime() + inviteTokenExpireTime);

		return Jwts.builder()
			.setHeader(headers)
			.setSubject(inviteType.toString())
			.setIssuer(ISS)
			.setIssuedAt(now)
			.claim("send", userId)
			.claim("email", email)
			.claim("role", role)
			.claim("workspaceId", workspaceId)
			.claim("groupId", groupId)
			.setExpiration(inviteTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();
	}

	@NoLogging
	public String createTrackToken(Long userId, Long organizationLicenseKeyId) {
		Date now = new Date();
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS512");

		Date tractTokenExpiresIn = new Date(now.getTime() + tractTokenExpireTime);

		return Jwts.builder()
			.setHeader(headers)
			.setSubject(userId.toString())
			.setIssuer(TRACK_ISS)
			.setIssuedAt(now)
			.claim(AUTHORITIES_KEY, TrackRole.ROLE_TRACK_USER.name())
			.claim("organizationLicenseKeyId", organizationLicenseKeyId)
			.setExpiration(tractTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();
	}

	@NoLogging
	public String getUserNameFromJwtToken(String token) {
		Claims claims = parseClaims(token);
		return claims.getSubject();
	}

	@NoLogging
	public String getUserEmailFromJwtToken(String token) {
		Claims claims = parseClaims(token);
		return claims.get("email").toString();
	}

	@NoLogging
	public Long getOrganizationIdFromJwtToken(String token) {
		Claims claims = parseClaims(token);
		return Long.valueOf(claims.get("organizationId").toString());
	}

	@NoLogging
	public Long getWorkspaceIdFromToken(String token) {
		Claims claims = parseClaims(token);
		return Long.valueOf(claims.get("workspaceId").toString());
	}

	public Long getSenderId(String inviteToken) {
		Claims claims = parseClaims(inviteToken);
		return Long.valueOf(claims.get("send").toString());
	}

	@NoLogging
	public Long getSendFromJwtToken(String token) {
		Claims claims = parseClaims(token);
		return Long.valueOf(claims.get("send").toString());
	}

	@NoLogging
	public String getInviteRoleFromJwtToken(String token) {
		Claims claims = parseClaims(token);
		return claims.get("role").toString();
	}

	@NoLogging
	public ZonedDateTime getExpireDateFromJwtToken(String token) {
		Claims claims = parseClaims(token);
		return ZonedDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
	}

	@NoLogging
	public Long getGroupIdFromJwtToken(String token) {
		Claims claims = parseClaims(token);
		return Long.valueOf(claims.get("groupId").toString());
	}

	@NoLogging
	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);

		if (claims.get(AUTHORITIES_KEY) == null) {
			throw new CustomException(INVALID_ROLE);
		}

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		UserDetails principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	public Authentication getAuthentication(CustomUserDetails customUserDetails) {
		return new UsernamePasswordAuthenticationToken(customUserDetails,
			"", customUserDetails.getAuthorities()
		);
	}

	public Authentication getAdminAuthentication(CustomAdminUserDetails customAdminUserDetails) {
		return new UsernamePasswordAuthenticationToken(customAdminUserDetails,
			"", customAdminUserDetails.getAuthorities()
		);
	}

	public Authentication getTrackAuthentication(CustomTrackUserDetails customTrackUserDetails) {
		return new UsernamePasswordAuthenticationToken(customTrackUserDetails,
			"", customTrackUserDetails.getAuthorities()
		);
	}

	public String getIssuerFromJwtToken(String token) {
		Claims claims = parseClaims(token);
		return claims.getIssuer();
	}

	public String getTokenFromHttpServletRequest(HttpServletRequest request) {
		String authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
		return getTokenFromAuthorizationHeader(authorizationHeaderValue);
	}

	public String getTokenFromAuthorizationHeader(String requestTokenHeader) {
		if (StringUtils.hasText(requestTokenHeader) && requestTokenHeader.startsWith(GRANT_TYPE_PREFIX)) {
			return requestTokenHeader.substring(GRANT_TYPE_PREFIX.length());
		}
		return null;
	}

	@NoLogging
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.info("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.info("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.info("JWT claims string is empty: {}", e.getMessage());
		} catch (Exception e) {
			log.info("token valid error: {}", e.getMessage());
		}

		return false;
	}

	@NoLogging
	public Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public String createAuthorizationHeaderValue(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}
}
