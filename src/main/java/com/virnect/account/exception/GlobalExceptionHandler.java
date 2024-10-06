package com.virnect.account.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import feign.FeignException;
import io.sentry.SentryEvent;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.ErrorResponse;
import com.virnect.account.config.message.localeMessageConverter;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final localeMessageConverter localeMessage;

	@ExceptionHandler(value = {CustomException.class})
	protected ResponseEntity<ErrorResponse> handleCustomException(
		CustomException e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(
			e.getErrorCode(), localeMessage.getMessage(e.getErrorCode().toString()), request);
	}

	@ExceptionHandler(value = {UsernameNotFoundException.class})
	protected ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
		UsernameNotFoundException e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(
			ErrorCode.INVALID_USER, localeMessage.getMessage(ErrorCode.INVALID_USER.toString()), request);
	}

	@ExceptionHandler(value = {AccessDeniedException.class})
	protected ResponseEntity<ErrorResponse> handleAccessDeniedException(
		AccessDeniedException e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(
			ErrorCode.INVALID_AUTHORIZATION, localeMessage.getMessage(ErrorCode.INVALID_AUTHORIZATION.toString()),
			request
		);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult(), request);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<ErrorResponse> handleConstraintViolationException(
		ConstraintViolationException e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(
			ErrorCode.INVALID_INPUT_VALUE, localeMessage.getMessage(ErrorCode.INVALID_INPUT_VALUE.toString()), request);
	}

	@ExceptionHandler(BindException.class)
	protected ResponseEntity<ErrorResponse> handleBindException(
		BindException e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(
			ErrorCode.INVALID_INPUT_VALUE, localeMessage.getMessage(ErrorCode.INVALID_INPUT_VALUE.toString()), request);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(
			ErrorCode.INVALID_INPUT_VALUE, localeMessage.getMessage(ErrorCode.INVALID_INPUT_VALUE.toString()), request);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	protected ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
		NoHandlerFoundException e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(
			ErrorCode.NOT_FOUND, localeMessage.getMessage(ErrorCode.NOT_FOUND.toString()), request);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
		HttpRequestMethodNotSupportedException e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(
			ErrorCode.METHOD_NOT_ALLOWED, localeMessage.getMessage(ErrorCode.METHOD_NOT_ALLOWED.toString()), request);
	}

	@ExceptionHandler(FeignException.class)
	protected ResponseEntity<ErrorResponse> handleFeignException(
		FeignException e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(ErrorCode.FEIGN_SERVER_ERROR, e.getMessage(), request);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(
		Exception e, HttpServletRequest request, HandlerMethod handlerMethod
	) {
		preHandleException(e, request, handlerMethod);
		return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), request);
	}

	private void putStackTraceAndControllerToMDC(HandlerMethod handlerMethod, Exception e) {
		StringWriter stackTraceContent = new StringWriter();
		e.printStackTrace(new PrintWriter(stackTraceContent));
		Class<?> controller = handlerMethod.getMethod().getDeclaringClass();

		MDC.put("stackTrace", stackTraceContent.toString());
		MDC.put("controller", controller.getName());
		MDC.put("methodName", handlerMethod.getMethod().getName());

		if (e.getCause() != null) {
			StringWriter causeStackTraceContent = new StringWriter();
			e.getCause().printStackTrace(new PrintWriter(causeStackTraceContent));
			MDC.put("thirdPartyStackTrace", causeStackTraceContent.toString());
		}
	}

	private void storeSentryEventInRequest(Exception e, HttpServletRequest request) {
		SentryEvent sentryEvent = new SentryEvent();
		sentryEvent.setThrowable(e);
		request.setAttribute("sentryEvent", sentryEvent);
	}

	private void preHandleException(Exception e, HttpServletRequest request, HandlerMethod handlerMethod) {
		putStackTraceAndControllerToMDC(handlerMethod, e);
		storeSentryEventInRequest(e, request);
	}
}
