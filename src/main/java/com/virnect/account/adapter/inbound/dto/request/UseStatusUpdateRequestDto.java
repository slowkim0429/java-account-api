package com.virnect.account.adapter.inbound.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.UseStatus;

@ApiModel
@Getter
@NoArgsConstructor
public class UseStatusUpdateRequestDto {
	@NotBlank
	@ApiModelProperty(value = "사용 상태", example = "USE", required = true)
	@CommonEnum(enumClass = UseStatus.class)
	private String status;

	private UseStatusUpdateRequestDto(String status) {
		this.status = status;
	}

	public static UseStatusUpdateRequestDto from(String status) {
		return new UseStatusUpdateRequestDto(status);
	}

	public UseStatus statusValueOf() {
		return UseStatus.valueOf(status);
	}
}
