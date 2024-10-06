package com.virnect.account.adapter.outbound.request;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.OriginType;

@Getter
@NoArgsConstructor
public class SentrySendDto {
	private String service;
	private OriginType originType;
	private String clientServiceName;
	private String url;
	private String queryString;
	private HttpMethod method;
	private String controller;
	private String methodName;
	private String requestBody;
	private int responseStatus;
	private String responseBody;
	private String authToken;
	private String thirdPartyStackTrace;
	private Long elapsedTime;

	@Builder
	public SentrySendDto(
		String service, OriginType originType, String clientServiceName, String url, String queryString,
		String method, String controller, String methodName, String requestBody, int responseStatus,
		String responseBody, String authToken, String thirdPartyStackTrace, Long elapsedTime
	) {
		this.service = service;
		this.originType = originType;
		this.clientServiceName = clientServiceName;
		this.url = url;
		this.queryString = queryString;
		this.method = StringUtils.isBlank(method) ? null : HttpMethod.valueOf(method.toUpperCase());
		this.controller = controller;
		this.methodName = methodName;
		this.requestBody = requestBody;
		this.responseStatus = responseStatus;
		this.responseBody = responseBody;
		this.authToken = authToken;
		this.thirdPartyStackTrace = thirdPartyStackTrace;
		this.elapsedTime = elapsedTime;
	}
}
