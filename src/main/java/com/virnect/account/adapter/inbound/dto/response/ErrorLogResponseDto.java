package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import org.springframework.http.HttpMethod;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.OriginType;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
public class ErrorLogResponseDto {

	@ApiModelProperty(value = "error log id", example = "1")
	private final Long id;

	@ApiModelProperty(value = "생성일", example = "2022-01-04T11:44:55")
	private final String createdDate;

	@ApiModelProperty(value = "생성자", example = "0")
	private final Long createdBy;

	@ApiModelProperty(value = "컨트롤러", example = "com.example.api.controller.ExampleController")
	private final String controller;

	@ApiModelProperty(value = "디바이스", example = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)...")
	private final String device;

	@ApiModelProperty(value = "경과시간(ms)", example = "2001")
	private final Long elapsedTime;

	@ApiModelProperty(value = "메서드(HttpMethod)", example = "POST")
	private final HttpMethod method;

	@ApiModelProperty(value = "메서드이름(백엔드 동작함수)", example = "signin")
	private final String methodName;

	@ApiModelProperty(value = "query 문자열", example = "select * form example")
	private final String queryString;

	@ApiModelProperty(value = "응답 코드", example = "500")
	private final Integer responseStatus;

	@ApiModelProperty(value = "서비스", example = "account-api")
	private final String service;

	@ApiModelProperty(value = "URL", example = "https://www.example.com")
	private final String url;

	@ApiModelProperty(value = "client service name", example = "Workspace")
	private String clientServiceName;

	@ApiModelProperty(value = "origin type", example = "SERVER")
	private OriginType originType;

	@QueryProjection
	public ErrorLogResponseDto(
		Long id, ZonedDateTime createdDate, Long createdBy, String controller, String device, Long elapsedTime,
		HttpMethod method, String methodName, String queryString, Integer responseStatus, String service, String url,
		String clientServiceName, OriginType originType
	) {
		this.id = id;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.createdBy = createdBy;
		this.controller = controller;
		this.device = device;
		this.elapsedTime = elapsedTime;
		this.method = method;
		this.methodName = methodName;
		this.queryString = queryString;
		this.responseStatus = responseStatus;
		this.service = service;
		this.url = url;
		this.clientServiceName = clientServiceName;
		this.originType = originType;
	}
}
