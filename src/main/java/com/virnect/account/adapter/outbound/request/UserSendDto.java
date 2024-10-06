package com.virnect.account.adapter.outbound.request;

import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.model.User;

@Getter
@Setter
public class UserSendDto {
	private Long userId;
	private String nickname;
	private String email;
	private String profileImage;
	private Color profileColor;
	private MembershipStatus status;
	private Long organizationId;
	private Long createdBy;
	private Long updatedBy;

	private UserSendDto(
		Long userId, String nickname, String email, String profileImage, Color profileColor, MembershipStatus status,
		Long organizationId, Long createdBy, Long lastModifiedBy
	) {
		this.userId = userId;
		this.nickname = nickname;
		this.email = email;
		this.profileImage = profileImage;
		this.profileColor = profileColor;
		this.status = status;
		this.organizationId = organizationId;
		this.createdBy = createdBy;
		this.updatedBy = lastModifiedBy;
	}

	public static UserSendDto from(User user) {
		return new UserSendDto(
			user.getId(),
			user.getNickname(),
			user.getEmail(),
			user.getProfileImage(),
			user.getProfileColor(),
			user.getStatus(),
			user.getOrganizationId(),
			user.getCreatedBy(),
			user.getLastModifiedBy()
		);
	}
}
