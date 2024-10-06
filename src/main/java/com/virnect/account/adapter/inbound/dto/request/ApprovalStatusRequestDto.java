package com.virnect.account.adapter.inbound.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.validate.ApprovalStatusSubset;
import com.virnect.account.domain.enumclass.ApprovalStatus;

@Getter
@ApiModel
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalStatusRequestDto {
	@NotBlank
	@ApiModelProperty(value = "승인 상태", example = "APPROVED")
	@ApprovalStatusSubset(anyOf = {ApprovalStatus.APPROVED, ApprovalStatus.REJECT})
	private String status;

	private ApprovalStatusRequestDto(String status) {
		this.status = status;
	}

	public static ApprovalStatusRequestDto from(String status) {
		return new ApprovalStatusRequestDto(status);
	}

	public ApprovalStatus statusValueOf() {
		return ApprovalStatus.valueOf(status);
	}
}
