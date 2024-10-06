package com.virnect.account.log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sentry.SentryEvent;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequest;
import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequestContextHolder;
import com.virnect.account.adapter.outbound.request.SentrySendDto;
import com.virnect.account.domain.enumclass.OriginType;
import com.virnect.account.domain.model.ErrorLog;
import com.virnect.account.port.inbound.ErrorLogService;
import com.virnect.account.port.inbound.SentryService;
import com.virnect.account.security.jwt.TokenProvider;

@Component
@RequiredArgsConstructor
public class ExceptionFilter implements Filter {
	private static final String USER_AGENT = "user-agent";
	private static final String CLIENT_SERVICE_NAME = "service-name";
	private static final String REGEXP_UNSUPPORTED_URL = "/swagger-ui/(.*)";
	private final ErrorLogService errorLogService;
	private final TokenProvider tokenProvider;
	private final ObjectMapper objectMapper;
	private final SentryService sentryService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException, ServletException {
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest)request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(
			(HttpServletResponse)response);

		long start = System.currentTimeMillis();

		CustomHttpServletRequest customHttpServletRequest = new CustomHttpServletRequest(requestWrapper);
		CustomHttpServletRequestContextHolder.httpServletRequestContext.set(customHttpServletRequest);

		chain.doFilter(requestWrapper, responseWrapper);
		int responseStatus = responseWrapper.getStatus();
		if (responseStatus != HttpStatus.OK.value()
			&& !((HttpServletRequest)request).getRequestURI().matches(REGEXP_UNSUPPORTED_URL)) {
			long elapsedTime = (System.currentTimeMillis() - start);

			ErrorLog errorLog = ErrorLog.builder()
				.service("account-api")
				.originType(OriginType.SERVER)
				.clientServiceName(((HttpServletRequest)request).getHeader(CLIENT_SERVICE_NAME))
				.url(((HttpServletRequest)request).getRequestURI())
				.queryString(((HttpServletRequest)request).getQueryString())
				.device(((HttpServletRequest)request).getHeader(USER_AGENT))
				.header(getHeaders((HttpServletRequest)request).toString())
				.method(((HttpServletRequest)request).getMethod())
				.controller(MDC.get("controller"))
				.methodName(MDC.get("methodName"))
				.requestBody(getRequestBody(requestWrapper))
				.responseBody(getResponseBody(responseWrapper))
				.responseStatus(responseStatus)
				.authToken(tokenProvider.getTokenFromHttpServletRequest((HttpServletRequest)request))
				.stackTrace(MDC.get("stackTrace"))
				.thirdPartyStackTrace(MDC.get("thirdPartyStackTrace"))
				.elapsedTime(elapsedTime)
				.build();
			errorLogService.create(errorLog);

			SentryEvent sentryEvent = (SentryEvent)request.getAttribute("sentryEvent");

			if (sentryEvent != null) {
				SentrySendDto sentrySendDto = SentrySendDto.builder()
					.service("account-api")
					.originType(OriginType.SERVER)
					.clientServiceName(((HttpServletRequest)request).getHeader(CLIENT_SERVICE_NAME))
					.url(((HttpServletRequest)request).getRequestURI())
					.queryString(((HttpServletRequest)request).getQueryString())
					.method(((HttpServletRequest)request).getMethod())
					.controller(MDC.get("controller"))
					.methodName(MDC.get("methodName"))
					.requestBody(getRequestBody(requestWrapper))
					.responseBody(getResponseBody(responseWrapper))
					.responseStatus(responseStatus)
					.authToken(tokenProvider.getTokenFromHttpServletRequest((HttpServletRequest)request))
					.thirdPartyStackTrace(MDC.get("thirdPartyStackTrace"))
					.elapsedTime(elapsedTime)
					.build();

				sentryService.sentryCaptureEvent(sentryEvent, sentrySendDto);
			}
		}

		responseWrapper.setCharacterEncoding(StandardCharsets.UTF_8.name());
		responseWrapper.copyBodyToResponse();

		CustomHttpServletRequestContextHolder.httpServletRequestContext.remove();
		MDC.clear();
	}

	private Map<String, String> getHeaders(HttpServletRequest request) {
		Map<String, String> headerMap = new HashMap<>();

		Enumeration<String> headerArray = request.getHeaderNames();
		while (headerArray.hasMoreElements()) {
			String headerName = headerArray.nextElement();
			headerMap.put(headerName, request.getHeader(headerName));
		}
		return headerMap;
	}

	private String getRequestBody(ContentCachingRequestWrapper request) throws JsonProcessingException {
		ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
		if (wrapper != null) {
			byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				return applyMasking(new String(buf, 0, buf.length, StandardCharsets.UTF_8));
			}
		}
		return null;
	}

	private String applyMasking(String requestBody) throws JsonProcessingException {
		String[] maskingTargets = {"password", "newPassword", "checkPassword"};
		String regexOfPasswordTarget = "password|newPassword|checkPassword";

		Map<String, Object> requestBodyMap = objectMapper.readValue(requestBody, new TypeReference<>() {
		});

		for (Map.Entry<String, Object> map : requestBodyMap.entrySet()) {
			for (String item : maskingTargets) {
				if (map.getKey().equalsIgnoreCase(item)) {
					if (item.matches(regexOfPasswordTarget)) {
						map.setValue("********************");
					} else {
						if (map.getValue() instanceof String) {
							map.setValue(((String)map.getValue()).replaceAll("(?<=.{0}).", "*"));
						}
					}
				}
			}
		}
		return requestBodyMap.toString();
	}

	private String getResponseBody(final HttpServletResponse response) {
		String payload = null;
		ContentCachingResponseWrapper wrapper =
			WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
		if (wrapper != null) {
			byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				payload = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
			}
		}
		return payload;
	}
}
