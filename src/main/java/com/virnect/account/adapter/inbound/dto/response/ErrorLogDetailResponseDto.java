package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import org.springframework.http.HttpMethod;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.OriginType;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class ErrorLogDetailResponseDto {

	@ApiModelProperty(value = "id", example = "1")
	private Long id;

	@ApiModelProperty(value = "created at", example = "2022-10-12T 12:30:54")
	private String createdAt;

	@ApiModelProperty(value = "updated at", example = "2022-10-12T 12:30:54")
	private String updatedAt;

	@ApiModelProperty(value = "created by", example = "0")
	private Long createdBy;

	@ApiModelProperty(value = "updated by", example = "0")
	private Long updatedBy;

	@ApiModelProperty(value = "auth token", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMDA...")
	private String authToken;

	@ApiModelProperty(value = "controller", example = "com.virnect.example.controller.ExampleController")
	private String controller;

	@ApiModelProperty(value = "device", example = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/...")
	private String device;

	@ApiModelProperty(value = "elapsed time", example = "2001")
	private Long elapsedTime;

	@ApiModelProperty(value = "header", example = "{sec-fetch-mode=cors, content-length=623, referer=https://devwo...")
	private String header;

	@ApiModelProperty(value = "method", example = "POST")
	private HttpMethod method;

	@ApiModelProperty(value = "method name", example = "signin")
	private String methodName;

	@ApiModelProperty(value = "query string", example = "select * form example")
	private String queryString;

	@ApiModelProperty(value = "request body", example = "{authCode=123456, email=example@virnect.com, inviteToken=...")
	private String requestBody;

	@ApiModelProperty(value = "response body", example = "{\"status\":500,\"error\":\"INTERNAL_SERVER_ERROR\",...")
	private String responseBody;

	@ApiModelProperty(value = "response status", example = "500")
	private Integer responseStatus;

	@ApiModelProperty(value = "service", example = "account-api")
	private String service;

	@ApiModelProperty(value = "client service name", example = "Workspace")
	private String clientServiceName;

	@ApiModelProperty(value = "stack trace", example = "... anyway stack trace")
	private String stackTrace;

	@ApiModelProperty(value = "third party stack trace", example = "... anyway stack trace")
	private String thirdPartyStackTrace;

	@ApiModelProperty(value = "url")
	private String url;

	@ApiModelProperty(value = "origin type", example = "SERVER")
	private OriginType originType;

	@QueryProjection
	public ErrorLogDetailResponseDto(
		Long id, ZonedDateTime createdAt, ZonedDateTime updatedAt, Long createdBy, Long updatedBy, String authToken,
		String controller, String device, Long elapsedTime, String header, HttpMethod method, String methodName,
		String queryString, String requestBody, String responseBody, Integer responseStatus, String service,
		String clientServiceName, String stackTrace, String thirdPartyStackTrace, String url, OriginType originType
	) {
		this.id = id;
		this.createdAt = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdAt);
		this.updatedAt = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedAt);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.authToken = authToken;
		this.controller = controller;
		this.device = device;
		this.elapsedTime = elapsedTime;
		this.header = header;
		this.method = method;
		this.methodName = methodName;
		this.queryString = queryString;
		this.requestBody = requestBody;
		this.responseBody = responseBody;
		this.responseStatus = responseStatus;
		this.service = service;
		this.clientServiceName = clientServiceName;
		this.stackTrace = stackTrace;
		this.thirdPartyStackTrace = thirdPartyStackTrace;
		this.url = url;
		this.originType = originType;
	}
}
