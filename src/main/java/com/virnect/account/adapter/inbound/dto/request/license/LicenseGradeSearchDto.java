package com.virnect.account.adapter.inbound.dto.request.license;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.LicenseGradeType;

@Setter
@Getter
@ApiModel
public class LicenseGradeSearchDto {
	@ApiModelProperty(value = "License grade 승인 상태")
	private ApprovalStatus status;

	@ApiModelProperty(value = "License grade 타입")
	private LicenseGradeType gradeType;
}
