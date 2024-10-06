package com.virnect.account.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import feign.Request;
import io.sentry.SentryEvent;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequest;
import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequestContextHolder;
import com.virnect.account.adapter.outbound.request.SentrySendDto;
import com.virnect.account.domain.enumclass.OriginType;
import com.virnect.account.domain.model.ErrorLog;
import com.virnect.account.port.inbound.ErrorLogService;
import com.virnect.account.port.inbound.SentryService;

@Component
@Aspect
@RequiredArgsConstructor
public class FeignErrorLogging {
	private static final String CLIENT_SERVICE_NAME = "service-name";
	private static final String USER_AGENT = "user-agent";

	private final ErrorLogService errorLogService;
	private final ObjectMapper objectMapper;
	private final SentryService sentryService;

	@Pointcut("within(com.virnect.account.port.inbound.ContractAPIService+) && @annotation(org.springframework.scheduling.annotation.Async)")
	public void asyncContractAPIService() {
	}

	@Pointcut("within(com.virnect.account.port.inbound.WorkspaceAPIService+) && @annotation(org.springframework.scheduling.annotation.Async)")
	public void asyncWorkspaceAPIService() {
	}

	@Pointcut("within(com.virnect.account.port.inbound.NotificationAPIService+) && @annotation(org.springframework.scheduling.annotation.Async)")
	public void asyncNotificationAPIService() {
	}

	@Around("asyncContractAPIService() || asyncWorkspaceAPIService() || asyncNotificationAPIService()")
	public Object asyncFeignLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		long start = System.currentTimeMillis();

		try {
			return proceedingJoinPoint.proceed();
		} catch (Exception e) {
			long elapsedTime = (System.currentTimeMillis() - start);

			CustomHttpServletRequest request = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();

			StringWriter stackTraceContent = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTraceContent));

			ErrorLog errorLog = ErrorLog.builder()
				.service("account-api")
				.originType(OriginType.SERVER)
				.clientServiceName(request.getHeader(CLIENT_SERVICE_NAME))
				.url(request.getUrl())
				.queryString(request.getQueryString())
				.device(request.getHeader(USER_AGENT))
				.header(String.valueOf(request.getHeaders()))
				.method(request.getMethod())
				.controller(proceedingJoinPoint.getSignature().getDeclaringTypeName())
				.methodName(proceedingJoinPoint.getSignature().getName())
				.requestBody(request.getRequestBody())
				.responseBody(null)
				.responseStatus(0)
				.authToken(request.getTokenFromAuthorizationHeader())
				.stackTrace(stackTraceContent.toString())
				.elapsedTime(elapsedTime)
				.build();
			errorLogService.create(errorLog);

			SentryEvent sentryEvent = new SentryEvent();
			sentryEvent.setThrowable(e);

			SentrySendDto sentrySendDto = SentrySendDto.builder()
				.service("account-api")
				.originType(OriginType.SERVER)
				.clientServiceName(request.getHeader(CLIENT_SERVICE_NAME))
				.url(request.getUrl())
				.queryString(request.getQueryString())
				.method(request.getMethod())
				.controller(proceedingJoinPoint.getSignature().getDeclaringTypeName())
				.methodName(proceedingJoinPoint.getSignature().getName())
				.requestBody(request.getRequestBody())
				.responseBody(null)
				.responseStatus(0)
				.authToken(request.getTokenFromAuthorizationHeader())
				.elapsedTime(elapsedTime)
				.build();

			sentryService.sentryCaptureEvent(sentryEvent, sentrySendDto);

			throw e;
		}
	}

	@Around("within(com.virnect.account.port.inbound.HubspotService+)")
	public Object syncFeignLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		long start = System.currentTimeMillis();

		try {
			return proceedingJoinPoint.proceed();
		} catch (FeignException e) {
			long elapsedTime = (System.currentTimeMillis() - start);

			Request request = e.request();

			StringWriter stackTraceContent = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTraceContent));

			ErrorLog errorLog = ErrorLog.builder()
				.service("account-api")
				.originType(OriginType.SERVER)
				.url(request.requestTemplate().url())
				.queryString(request.requestTemplate().queryLine())
				.device(null)
				.header(request.headers().toString())
				.method(request.httpMethod().name())
				.controller(proceedingJoinPoint.getSignature().getDeclaringTypeName())
				.methodName(proceedingJoinPoint.getSignature().getName())
				.requestBody(getRequestBody(request.body()))
				.responseBody(e.responseBody().isEmpty() ? null : new String(e.responseBody().get().array()))
				.responseStatus(e.status())
				.authToken(
					request.headers()
						.getOrDefault(HttpHeaders.AUTHORIZATION, Collections.emptyList()).toString())
				.stackTrace(stackTraceContent.toString())
				.elapsedTime(elapsedTime)
				.build();
			errorLogService.create(errorLog);

			SentryEvent sentryEvent = new SentryEvent();
			sentryEvent.setThrowable(e);

			SentrySendDto sentrySendDto = SentrySendDto.builder()
				.service("account-api")
				.originType(OriginType.SERVER)
				.url(request.requestTemplate().url())
				.queryString(request.requestTemplate().queryLine())
				.method(request.httpMethod().name())
				.controller(proceedingJoinPoint.getSignature().getDeclaringTypeName())
				.methodName(proceedingJoinPoint.getSignature().getName())
				.requestBody(getRequestBody(request.body()))
				.responseBody(e.responseBody().isEmpty() ? null : new String(e.responseBody().get().array()))
				.responseStatus(e.status())
				.authToken(
					request.headers()
						.getOrDefault(HttpHeaders.AUTHORIZATION, Collections.emptyList()).toString())
				.elapsedTime(elapsedTime)
				.build();

			sentryService.sentryCaptureEvent(sentryEvent, sentrySendDto);

			throw e;
		}
	}

	private String getRequestBody(byte[] body) {
		String requestBody = new String(body, 0, body.length, StandardCharsets.UTF_8);
		try {
			return objectMapper.readValue(requestBody, new TypeReference<>() {
			}).toString();
		} catch (JsonProcessingException e) {
			return null;
		}
	}
}
