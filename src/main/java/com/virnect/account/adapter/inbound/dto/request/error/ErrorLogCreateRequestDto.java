package com.virnect.account.adapter.inbound.dto.request.error;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.http.HttpMethod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;

@Getter
@Setter
@ApiModel
public class ErrorLogCreateRequestDto {
	@ApiModelProperty(value = "요청 클라이어트 service name", example = "SQUARS workspace")
	private String clientServiceName;
	@Size(max = 250)
	@NotBlank
	@ApiModelProperty(value = "요청 url", required = true)
	private String url;
	@Size(max = 250)
	@ApiModelProperty(value = "query 문자열", example = "?id=1234")
	private String queryString;
	@ApiModelProperty(value = "요청 헤더 정보", example = "")
	private String header;
	@ApiModelProperty(value = "요청 메서드(HttpMethod)", example = "POST")
	@CommonEnum(enumClass = HttpMethod.class)
	private String httpMethod;
	@ApiModelProperty(value = "요청 디바이스", example = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)...")
	private String device;
	@ApiModelProperty(value = "에러 발생 메서드이름", example = "getScene")
	private String methodName;
	@ApiModelProperty(value = "요청 데이터", example = "\"{\"name\":\"john\"}\"")
	private String requestBody;
	@ApiModelProperty(value = "응답 코드", example = "0")
	private Integer responseStatus;
	@ApiModelProperty(value = "인증 토큰(access token)")
	private String authToken;
	@ApiModelProperty(value = "에러 발생 stack strace")
	private String stackTrace;
}
