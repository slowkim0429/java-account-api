package com.virnect.account.adapter.inbound.dto.request.updateguide;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class UpdateGuideMediaRequestDto {

	@NotNull
	@ApiModelProperty(value = "file", required = true)
	private MultipartFile file;
}
