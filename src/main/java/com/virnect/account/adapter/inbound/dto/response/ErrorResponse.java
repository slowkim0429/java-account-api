package com.virnect.account.adapter.inbound.dto.response;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.exception.ErrorCode;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@NoArgsConstructor
public class ErrorResponse {
	private final String timestamp = ZonedDateTimeUtil.convertToString(ZonedDateTimeUtil.zoneOffsetOfUTC());
	private int status;
	private String error;
	private String customError;
	private String message;
	private String path;
	private List<FieldError> errors;

	@Builder
	private ErrorResponse(
		int status, String error, String customError, String message, String path, List<FieldError> errors
	) {
		this.status = status;
		this.error = error;
		this.customError = customError;
		this.message = message;
		this.path = path;
		this.errors = errors;
	}

	public static ErrorResponse parseError(ErrorCode errorCode, String message) {
		return ErrorResponse.builder()
			.status(errorCode.getHttpStatus().value())
			.error(errorCode.getHttpStatus().name())
			.customError(errorCode.name())
			.message(message)
			.build();
	}

	public static ErrorResponse parseError(int status, String error, String message) {
		return ErrorResponse.builder()
			.status(status)
			.error(error)
			.message(message)
			.build();
	}

	public static ResponseEntity<ErrorResponse> toResponseEntity(int status, String error, String message) {
		return ResponseEntity
			.status(status)
			.body(ErrorResponse.builder()
				.status(status)
				.error(error)
				.message(message)
				.build()
			);
	}

	public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.builder()
				.status(errorCode.getHttpStatus().value())
				.error(errorCode.getHttpStatus().name())
				.customError(errorCode.name())
				.message("")
				.build()
			);
	}

	public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, HttpServletRequest request) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.builder()
				.status(errorCode.getHttpStatus().value())
				.error(errorCode.getHttpStatus().name())
				.customError(errorCode.name())
				.message("")
				.path(request.getRequestURI())
				.build()
			);
	}

	public static ResponseEntity<ErrorResponse> toResponseEntity(
		ErrorCode errorCode, String message, HttpServletRequest request
	) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.builder()
				.status(errorCode.getHttpStatus().value())
				.error(errorCode.getHttpStatus().name())
				.customError(errorCode.name())
				.message(message)
				.path(request.getRequestURI())
				.build()
			);
	}

	public static ResponseEntity<ErrorResponse> toResponseEntity(
		ErrorCode errorCode, final BindingResult bindingResult, HttpServletRequest request
	) {
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		String message = fieldErrors.get(0) == null ? "" : fieldErrors.get(0).getDefaultMessage();
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.builder()
				.status(errorCode.getHttpStatus().value())
				.error(errorCode.getHttpStatus().name())
				.customError(errorCode.name())
				.message(message)
				.errors(fieldErrors)
				.path(request.getRequestURI())
				.build()
			);
	}
}
