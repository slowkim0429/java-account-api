package com.virnect.account.security.config;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.JwtFilter;
import com.virnect.account.security.jwt.TokenProvider;
import com.virnect.account.security.service.CustomAdminUserDetailsService;
import com.virnect.account.security.service.CustomTrackUserDetailsService;
import com.virnect.account.security.service.CustomUserDetailsService;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	private final TokenProvider tokenProvider;
	private final CustomUserDetailsService customUserDetailsService;
	private final CustomAdminUserDetailsService customAdminUserDetailsService;
	private final CustomTrackUserDetailsService customTrackUserDetailsService;
	private final RedisRepository redisRepository;

	@Override
	public void configure(HttpSecurity http) {
		JwtFilter customFilter = new JwtFilter(
			tokenProvider, customUserDetailsService, customAdminUserDetailsService, customTrackUserDetailsService,
			redisRepository
		);
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
