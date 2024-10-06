package com.virnect.account.adapter.inbound.dto.request.organization;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.domain.enumclass.AcceptOrReject;

@Setter
@Getter
@NoArgsConstructor
public class OrganizationChangeMarketInfoDto {
	@ApiModelProperty(value = "광고 수신 동의 여부(ACCEPT, REJECT)", required = true, example = "ACCEPT")
	@NotNull(message = "광고 수신 동의 여부 정보는 반드시 입력되어야 합니다.")
	private AcceptOrReject marketInfoReceive;
}
