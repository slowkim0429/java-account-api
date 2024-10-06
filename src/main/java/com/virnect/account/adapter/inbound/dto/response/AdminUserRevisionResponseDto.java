package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.model.AdminUser;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class AdminUserRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "admin user id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "닉네임", example = "user")
	private String nickname;

	@ApiModelProperty(value = "이메일", example = "user@virnect.com")
	private String email;

	@ApiModelProperty(value = "프로필 이미지", example = "null")
	private String profileImage;

	@ApiModelProperty(value = "프로필 색상", example = "TEAL")
	private Color profileColor;

	@ApiModelProperty(value = "Locale Id", example = "1000000118")
	private Long localeId;

	@ApiModelProperty(value = "Locale Code", example = "KR")
	private String localeCode;

	@ApiModelProperty(value = "Region Id", example = "1000000000")
	private Long regionId;

	@ApiModelProperty(value = "Region Code", example = "KR")
	private String regionCode;

	@ApiModelProperty(value = "Region AWS Code", example = "Asia Pacific (Seoul)")
	private String regionAwsCode;

	@ApiModelProperty(value = "Language", example = "en")
	private String language;

	@ApiModelProperty(value = "어드민 가입 신청 승인 상태", example = "REGISTER")
	private ApprovalStatus approvalStatus;

	@ApiModelProperty(value = "어드민 사용자 이용 가입 상태", example = "JOIN")
	private MembershipStatus status;

	@ApiModelProperty(value = "생성 일자", example = "2022-01-04 11:44:55")
	private String createdDate;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-04 11:44:55")
	private String updatedDate;

	@ApiModelProperty(value = "Created By User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "Updated By User Id", example = "1000000000")
	private Long updatedBy;

	private AdminUserRevisionResponseDto(RevisionType revisionType, AdminUser adminUser) {
		this.revisionType = revisionType;
		this.id = adminUser.getId();
		this.nickname = adminUser.getNickname();
		this.email = adminUser.getEmail();
		this.profileImage = adminUser.getProfileImage();
		this.profileColor = adminUser.getProfileColor();
		this.localeId = adminUser.getLocaleId();
		this.localeCode = adminUser.getLocaleCode();
		this.regionId = adminUser.getRegionId();
		this.regionCode = adminUser.getRegionCode();
		this.regionAwsCode = adminUser.getRegionAwsCode();
		this.language = adminUser.getLanguage();
		this.approvalStatus = adminUser.getApprovalStatus();
		this.status = adminUser.getStatus();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(adminUser.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(adminUser.getUpdatedDate());
		this.createdBy = adminUser.getCreatedBy();
		this.updatedBy = adminUser.getLastModifiedBy();
	}

	public static AdminUserRevisionResponseDto of(Byte representation, AdminUser adminUser) {
		return new AdminUserRevisionResponseDto(RevisionType.valueOf(representation), adminUser);
	}
}
