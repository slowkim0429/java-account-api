package com.virnect.account.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private final ErrorCode errorCode;
	private final String message;

	public CustomException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.message = "";
	}

	public CustomException(ErrorCode errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}
}
