package com.virnect.account.adapter.inbound.dto.request.user;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.AcceptOrReject;

@Setter
@Getter
public class UserUpdateRequestDto {
	@ApiModelProperty(value = "닉네임", example = "닉넴")
	@Size(max = 30)
	private String name;

	@ApiModelProperty(value = "언어설정", example = "1000000000")
	@Min(1000000000)
	private Long localeId;

	@CommonEnum(enumClass = AcceptOrReject.class)
	@ApiModelProperty(value = "광고 수신 동의 여부", example = "ACCEPT")
	private String marketInfoReceive;

	@ApiModelProperty(value = "time zone id", example = "Africa/Lome")
	@Size(max = 50)
	private String zoneId;

	@ApiModelProperty(hidden = true)
	public boolean isInvalid() {
		return StringUtils.isBlank(name)
			&& StringUtils.isBlank(marketInfoReceive)
			&& localeId == null
			&& StringUtils.isBlank(zoneId);
	}

	@ApiModelProperty(hidden = true)
	public String getInvalidMessage() {
		return "수정하려는 내용을 입력해주세요";
	}
}
