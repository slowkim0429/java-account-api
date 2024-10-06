package com.virnect.account.adapter.inbound.dto.request.updateguide;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateGuideSearchDto {
	@ApiModelProperty(value = "update guide id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "이름", example = "Update grade name for admin")
	private String name;

	@ApiModelProperty(value = "노출 여부", example = "true")
	private Boolean isExposed;
}
