package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@ApiModel
@Getter
public class UpdateGuideMediaResponseDto {

	@ApiModelProperty(name = "fileUrl")
	private String fileUrl;

	public UpdateGuideMediaResponseDto(String fileUrl) {
		this.fileUrl = fileUrl;
	}
}
