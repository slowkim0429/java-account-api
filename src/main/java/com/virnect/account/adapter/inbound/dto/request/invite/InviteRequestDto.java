package com.virnect.account.adapter.inbound.dto.request.invite;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.enumclass.InviteRole;
import com.virnect.account.domain.enumclass.InviteType;

@Getter
@NoArgsConstructor
public class InviteRequestDto {
	@ApiModelProperty(value = "이메일", example = "user@virnect.com", required = true)
	@NotEmpty(message = "초대 할 사용자의 이메일 정보는 최소 하나 이상 입력되어야 합니다.")
	private List<String> emailList;

	@CommonEnum(enumClass = InviteRole.class)
	@NotBlank(message = "invite role은 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "invite role", example = "ROLE_WORKSPACE_USER", required = true)
	private String inviteRole;

	@Min(value = 1000000000, message = "workspace id 값은 1000000000보다 같거나 커야 합니다.")
	@NotNull(message = "workspace id는 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "workspace id", example = "1000000000", required = true)
	private Long workspaceId;

	@Min(value = 1000000000, message = "organization id 값은 1000000000보다 같거나 커야 합니다.")
	@NotNull(message = "organization id는 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "organization id", example = "1000000000", required = true)
	private Long organizationId;

	@Min(value = 1000000000, message = "group id 값은 1000000000보다 같거나 커야 합니다.")
	@ApiModelProperty(value = "group id", example = "1000000000")
	private Long groupId;

	@ApiModelProperty(value = "workspace name", example = "virnect")
	private String workspaceName;

	@ApiModelProperty(value = "group name", example = "virnect")
	private String groupName;

	@CommonEnum(enumClass = InviteType.class)
	@NotBlank(message = "invite type은 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "invite type", example = "WORKSPACE", required = true)
	private String inviteType;

	@ApiModelProperty(value = "profile image", example = "https://devfile.squars.io/virnect-platform/....ayHwiqgSnXk.png")
	private String profileImage;

	@CommonEnum(enumClass = Color.class)
	@ApiModelProperty(value = "profileColor", example = "BLUE")
	private String profileColor;

	private InviteRequestDto(
		List<String> emailList, String inviteRole, Long workspaceId, Long organizationId, String workspaceName,
		String inviteType, String profileImage
	) {

		this.emailList = emailList;
		this.inviteRole = inviteRole;
		this.workspaceId = workspaceId;
		this.organizationId = organizationId;
		this.workspaceName = workspaceName;
		this.inviteType = inviteType;
		this.profileImage = profileImage;
	}

	public static InviteRequestDto of(
		List<String> emailList, String inviteRole, Long workspaceId, Long organizationId, String workspaceName,
		String inviteType, String profileImage
	) {
		return new InviteRequestDto(
			emailList, inviteRole, workspaceId, organizationId, workspaceName, inviteType, profileImage);
	}

	@ApiModelProperty(hidden = true)
	public boolean isValid() {
		return emailList.size() == emailList.stream().distinct().count();
	}

	@ApiModelProperty(hidden = true)
	public String getInvalidMessage() {
		return "초대할 사용자의 이메일은 중복되어서는 안됩니다.";
	}

	@ApiModelProperty(hidden = true)
	public InviteType inviteTypeValueOf() {
		return InviteType.valueOf(this.inviteType);
	}

	@ApiModelProperty(hidden = true)
	public InviteRole inviteRoleValueOf() {
		return InviteRole.valueOf(this.inviteRole);
	}
}
