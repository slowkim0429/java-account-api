package com.virnect.account.adapter.inbound.dto.request;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomHttpServletRequest {
	private String url;
	private String queryString;
	private Map<String, String> header;
	private String method;
	private String authorizationHeaderValue;
	private String tokenFromAuthorizationHeader;
	private String requestBody;

	public CustomHttpServletRequest(
		HttpServletRequest request
	) {
		ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper)request;

		this.url = request.getRequestURI();
		this.queryString = request.getQueryString();
		this.header = parseHeader(request);
		this.method = request.getMethod();
		this.authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
		this.tokenFromAuthorizationHeader = getTokenFromAuthorizationHeader(authorizationHeaderValue);
		this.requestBody = getRequestBody(requestWrapper);
	}

	private String getTokenFromAuthorizationHeader(String requestTokenHeader) {
		if (StringUtils.hasText(requestTokenHeader) && requestTokenHeader.startsWith("Bearer ")) {
			return requestTokenHeader.substring("Bearer ".length());
		}
		return null;
	}

	private Map<String, String> parseHeader(HttpServletRequest request) {
		Map<String, String> headerMap = new HashMap<>();

		Enumeration<String> headerArray = request.getHeaderNames();
		while (headerArray.hasMoreElements()) {
			String headerName = headerArray.nextElement();
			headerMap.put(headerName, request.getHeader(headerName));
		}
		return headerMap;
	}

	private String getRequestBody(ContentCachingRequestWrapper request) {
		ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
		if (wrapper != null) {
			byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				String requestBody = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
				try {
					return new ObjectMapper().readValue(requestBody, new TypeReference<>() {
					}).toString();
				} catch (JsonProcessingException e) {
					return null;
				}
			}
		}

		return null;
	}

	public Map<String, String> getHeaders() {
		return this.header;
	}

	public String getHeader(String headerKeyName) {
		return header.get(headerKeyName);
	}
}
