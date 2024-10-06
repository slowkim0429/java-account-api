package com.virnect.account.adapter.inbound.dto.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.MembershipStatus;

@Setter
@Getter
public class AdminUserSearchDto {
	@ApiModelProperty(value = "Approval Status")
	private ApprovalStatus approvalStatus;

	@ApiModelProperty(value = "Membership Status")
	private MembershipStatus status;

	@ApiModelProperty(value = "Admin User Id")
	private Long adminUserId;

	@ApiModelProperty(value = "Authority group id")
	private Long authorityGroupId;

	@ApiModelProperty(value = "Email")
	private String email;
}
