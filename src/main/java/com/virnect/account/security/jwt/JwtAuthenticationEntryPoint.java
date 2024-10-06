package com.virnect.account.security.jwt;

import static com.virnect.account.exception.ErrorCode.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.ErrorResponse;
import com.virnect.account.config.message.localeMessageConverter;
import com.virnect.account.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper objectMapper;
	private final localeMessageConverter localeMessage;

	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException
	) throws IOException, ServletException {

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter()
			.println(objectMapper.writeValueAsString(
				ErrorResponse.parseError(INVALID_AUTHENTICATION, localeMessage.getMessage(ErrorCode.INVALID_AUTHENTICATION.toString())))
			);
	}
}