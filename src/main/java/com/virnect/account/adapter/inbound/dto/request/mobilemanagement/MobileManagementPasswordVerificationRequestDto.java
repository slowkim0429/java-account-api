package com.virnect.account.adapter.inbound.dto.request.mobilemanagement;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.log.NoLogging;

@ApiModel
@Getter
@NoLogging
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MobileManagementPasswordVerificationRequestDto {

	@ApiModelProperty(value = "비밀번호", example = "12345678", required = true)
	@Size(min = 8, max = 20)
	@NotBlank
	private String password;

	public MobileManagementPasswordVerificationRequestDto(String password) {
		this.password = password;
	}
}
