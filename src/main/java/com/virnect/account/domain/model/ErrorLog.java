package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.error.ErrorLogCreateRequestDto;
import com.virnect.account.domain.enumclass.OriginType;

@Entity
@Getter
@Table(name = "errors",
	indexes = {
		@Index(name = "idx_service", columnList = "service"),
		@Index(name = "idx_origin_type", columnList = "origin_type"),
		@Index(name = "idx_url", columnList = "url"),
		@Index(name = "idx_device", columnList = "device"),
		@Index(name = "idx_method", columnList = "method"),
		@Index(name = "idx_response_status", columnList = "response_status"),
		@Index(name = "idx_method_name", columnList = "method_name"),
		@Index(name = "idx_created_by", columnList = "created_by")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorLog extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "service", nullable = false, length = 50)
	private String service;

	@Column(name = "origin_type", length = 20)
	@Enumerated(EnumType.STRING)
	private OriginType originType;

	@Column(name = "client_service_name", length = 50)
	private String clientServiceName;

	@Column(name = "url", nullable = false, length = 250)
	private String url;

	@Column(name = "query_string", length = 1000)
	private String queryString;

	@Column(name = "device", length = 300)
	private String device;

	@Column(name = "header", columnDefinition = "TEXT")
	private String header;

	@Column(name = "method", length = 100)
	@Enumerated(EnumType.STRING)
	private HttpMethod method;

	@Column(name = "controller", length = 100)
	private String controller;

	@Column(name = "method_name", length = 100)
	private String methodName;

	@Column(name = "request_body", columnDefinition = "TEXT")
	private String requestBody;

	@Column(name = "response_status")
	private int responseStatus;

	@Column(name = "response_body", columnDefinition = "TEXT")
	private String responseBody;

	@Column(name = "auth_token", length = 1000)
	private String authToken;

	@Column(name = "stack_trace", columnDefinition = "TEXT")
	private String stackTrace;

	@Column(name = "third_party_stack_trace", columnDefinition = "TEXT")
	private String thirdPartyStackTrace;

	@Column(name = "elapsed_time")
	private Long elapsedTime;

	@Builder
	public ErrorLog(
		String service, OriginType originType, String clientServiceName, String url, String queryString, String device,
		String header,
		String controller,
		String methodName, String method, String requestBody, String responseBody, int responseStatus,
		String stackTrace, String thirdPartyStackTrace, String authToken, Long elapsedTime
	) {
		this.service = service;
		this.originType = originType;
		this.clientServiceName = clientServiceName;
		this.url = url;
		this.queryString = queryString;
		this.device = device;
		this.header = header;
		this.method = StringUtils.isBlank(method) ? null : HttpMethod.valueOf(method.toUpperCase());
		this.methodName = methodName;
		this.controller = controller;
		this.requestBody = requestBody;
		this.responseBody = responseBody;
		this.responseStatus = responseStatus;
		this.stackTrace = stackTrace;
		this.thirdPartyStackTrace = thirdPartyStackTrace;
		this.authToken = authToken;
		this.elapsedTime = elapsedTime;
	}

	public static ErrorLog clientErrorLog(ErrorLogCreateRequestDto errorLogCreateRequestDto) {
		return ErrorLog.builder()
			.service("account-api")
			.originType(OriginType.CLIENT)
			.clientServiceName(errorLogCreateRequestDto.getClientServiceName())
			.url(errorLogCreateRequestDto.getUrl())
			.queryString(errorLogCreateRequestDto.getQueryString())
			.header(errorLogCreateRequestDto.getHeader())
			.method(errorLogCreateRequestDto.getHttpMethod())
			.device(errorLogCreateRequestDto.getDevice())
			.methodName(errorLogCreateRequestDto.getMethodName())
			.requestBody(errorLogCreateRequestDto.getRequestBody())
			.responseStatus(errorLogCreateRequestDto.getResponseStatus())
			.authToken(errorLogCreateRequestDto.getAuthToken())
			.stackTrace(errorLogCreateRequestDto.getStackTrace())
			.build();
	}
}
