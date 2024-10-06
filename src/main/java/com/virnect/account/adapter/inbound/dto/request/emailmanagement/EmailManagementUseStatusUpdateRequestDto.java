package com.virnect.account.adapter.inbound.dto.request.emailmanagement;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.UseStatus;

@Setter
@Getter
@ApiModel
public class EmailManagementUseStatusUpdateRequestDto {
	@ApiModelProperty(value = "Use Status", example = "USE")
	@NotNull
	private UseStatus useStatus;
}

