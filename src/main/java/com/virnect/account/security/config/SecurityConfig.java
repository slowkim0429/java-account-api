package com.virnect.account.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.JwtAccessDeniedHandler;
import com.virnect.account.security.jwt.JwtAuthenticationEntryPoint;
import com.virnect.account.security.jwt.TokenProvider;
import com.virnect.account.security.service.CustomAdminUserDetailsService;
import com.virnect.account.security.service.CustomTrackUserDetailsService;
import com.virnect.account.security.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] AUTH_WHITELIST = {
		// -- Swagger UI v2
		"/v2/api-docs",
		"/swagger-resources",
		"/swagger-resources/**",
		"/configuration/ui",
		"/configuration/security",
		"/swagger-ui.html",
		"/webjars/**",
		// -- Swagger UI v3 (OpenAPI)
		"/v3/api-docs/**",
		"/swagger-ui/**",
		"/ping",
		"/actuator/prometheus"
	};
	private final TokenProvider tokenProvider;
	private final CustomUserDetailsService customUserDetailsService;
	private final CustomAdminUserDetailsService customAdminUserDetailsService;
	private final CustomTrackUserDetailsService customTrackUserDetailsService;
	private final RedisRepository redisRepository;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring()
			.antMatchers(AUTH_WHITELIST);
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.cors()
			.disable()
			.csrf()
			.disable()

			// exception handling 할 때 우리가 만든 클래스를 추가
			.exceptionHandling()
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.accessDeniedHandler(jwtAccessDeniedHandler)

			// 시큐리티는 기본적으로 세션을 사용
			// 여기서는 세션을 사용하지 않기 때문에 세션 설정을 Stateless 로 설정
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			// 로그인, 회원가입 API 는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
			.and()
			.authorizeRequests()
			.antMatchers("/api/auth/**")
			.permitAll()
			.antMatchers("/api/admin/auth/**")
			.permitAll()
			.antMatchers("/api/invites/assign")
			.permitAll()
			.antMatchers(
				HttpMethod.POST, "/api/payments/**", "/api/hubspot/**",
				"/api/event-popups/{eventPopupId}/coupons/email"
			)
			.permitAll()
			.antMatchers(
				HttpMethod.GET, "/api/locales", "/api/domains", "/api/items", "/api/update-guides",
				"/api/event-popups/event-types/*/service-types/*/latest", "/api/mobile-managements/notices/expose",
				"/api/mobile-managements/force-update-minimum-versions"
			)
			.permitAll()
			.antMatchers(AUTH_WHITELIST)
			.permitAll()
			.anyRequest()
			.authenticated()   // 나머지 API 는 전부 인증 필요

			// JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스를 적용
			.and()
			.apply(new JwtSecurityConfig(
				tokenProvider, customUserDetailsService, customAdminUserDetailsService, customTrackUserDetailsService,
				redisRepository
			));
	}
}
