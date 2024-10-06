package com.virnect.account.adapter.inbound.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InviteUserAssignRequestDto {
	@ApiModelProperty(
		value = "inviteToken", position = 1, name = "inviteToken", required = true,
		example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMDAwMDAwMDAwIiwiaXNzIjoidmlybmVjdCIsImlhdCI6MTY0MjU2NjA1MCwiYWNjb3VudCI6MTAwMDAwMDAwMCwic2VuZCI6MTAwMDAwMDAwMSwibG9jYWxlIjoia29fS1IiLCJlbWFpbCI6InRlc3RAdmlybmVjdC5jb20iLCJleHAiOjE2NDI2NTI0NTB9.aymGNfnnwQDefVd7mVwtEhZVADwNdWRFqKazaPKDuTexBoo3G9-BsMTm5Nqf8-COvEx1XosPaiO4-M71dS1OBQ"
	)
	@NotBlank
	@Size(min = 100, max = 1000)
	private String inviteToken;

	public InviteUserAssignRequestDto(String inviteToken) {
		this.inviteToken = inviteToken;
	}
}
