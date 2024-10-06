package com.virnect.account.adapter.inbound.dto.request.authoritygroup;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.UseStatus;

@Getter
@Setter
@ApiModel
public class AuthorityGroupSearchDto {
	@CommonEnum(enumClass = UseStatus.class)
	private String status;

	public UseStatus statusValueOf() {
		return StringUtils.isBlank(status) ? null : UseStatus.valueOf(status);
	}
}
