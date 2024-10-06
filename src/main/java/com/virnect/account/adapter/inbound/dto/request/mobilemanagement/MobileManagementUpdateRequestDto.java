package com.virnect.account.adapter.inbound.dto.request.mobilemanagement;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.log.NoLogging;

@ApiModel
@Getter
@Setter
@NoLogging
public class MobileManagementUpdateRequestDto {

	@ApiModelProperty(value = "변경 할 비밀번호", example = "12@asdf34")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[,./!@#$%])[A-Za-z\\d,./!@#$%]{8,20}$")
	private String newPassword;

	@ApiModelProperty(value = "공지 message", example = "금일 1시 부터 3시 까지 서버 긴급 점검을 수행합니다.")
	@Size(max = 200)
	private String message;

	@ApiModelProperty(value = "공지 노출 여부", example = "true", required = true)
	@NotNull
	private Boolean isExposed;

	@AssertTrue
	@ApiModelProperty(hidden = true)
	public boolean isValidMessage() {
		if (Boolean.TRUE.equals(isExposed)) {
			return StringUtils.isNotBlank(message);
		}
		return true;
	}

	@ApiModelProperty(hidden = true)
	public boolean isExistNewPassword() {
		return StringUtils.isNotBlank(newPassword);
	}
}
