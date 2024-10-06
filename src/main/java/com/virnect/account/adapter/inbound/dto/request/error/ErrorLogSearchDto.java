package com.virnect.account.adapter.inbound.dto.request.error;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.OriginType;

@Getter
@Setter
@ApiModel
public class ErrorLogSearchDto {

	@ApiModelProperty(value = "응답 상태", example = "500")
	private Integer responseStatus;

	@ApiModelProperty(value = "http method", example = "POST")
	private HttpMethod method;

	@ApiModelProperty(value = "url", example = "/error-logs")
	private String url;

	@ApiModelProperty(value = "메서드 이름", example = "getErrorLog")
	private String methodName;

	@ApiModelProperty(value = "생성자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "origin type", example = "SERVER")
	@CommonEnum(enumClass = OriginType.class)
	private String originType;

	@ApiModelProperty(hidden = true)
	public OriginType originTypeValueOf() {
		return StringUtils.isBlank(originType) ? null : OriginType.valueOf(originType);
	}
}
