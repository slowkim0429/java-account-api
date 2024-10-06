package com.virnect.account.adapter.inbound.dto.request.emailmanagement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.enumclass.UseStatus;

@Setter
@Getter
@ApiModel
public class EmailManagementSearchDto {
	@ApiModelProperty(value = "Email Type", example = "WELCOME")
	private Mail emailType;
	@ApiModelProperty(value = "Use Status", example = "USE")
	private UseStatus useStatus;
}

