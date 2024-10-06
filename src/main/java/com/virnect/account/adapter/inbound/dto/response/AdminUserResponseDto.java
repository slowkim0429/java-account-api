package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class AdminUserResponseDto {
	@ApiModelProperty(value = "user id", example = "1000000000")
	private Long id;
	@ApiModelProperty(value = "닉네임", example = "test")
	private String nickname;
	@ApiModelProperty(value = "이메일", example = "test@virnect.com")
	private String email;
	@ApiModelProperty(value = "프로필 이미지", example = "null")
	private String profileImage;
	@ApiModelProperty(value = "프로필 색상", example = "TEAL")
	private Color profileColor;
	@ApiModelProperty(value = "Locale Id", example = "1000000118")
	private Long localeId;
	@ApiModelProperty(value = "Locale Code", example = "KR")
	private String localeCode;
	@ApiModelProperty(value = "Region Id", example = "test@virnect.com")
	private Long regionId;
	@ApiModelProperty(value = "Region Code", example = "KR")
	private String regionCode;
	@ApiModelProperty(value = "Region AWS Code", example = "ap-northeast-2")
	private String regionAwsCode;
	@ApiModelProperty(value = "Language", example = "en")
	private String language;
	@ApiModelProperty(value = "어드민 가입 신청 승인 상태", example = "REGISTER")
	private ApprovalStatus approvalStatus;
	@ApiModelProperty(value = "어드민 사용자 이용 가입 상태", example = "JOIN")
	private MembershipStatus status;
	@ApiModelProperty(value = "Authority group Id", example = "10000000000")
	private String authorityGroupName;
	@ApiModelProperty(value = "생성 일자", example = "2022-01-04T11:44:55")
	private String createdDate;
	@ApiModelProperty(value = "수정 일자", example = "2022-01-04T11:44:55")
	private String updatedDate;
	@ApiModelProperty(value = "Created By User Id", example = "1000000000")
	private Long createdBy;
	@ApiModelProperty(value = "Updated By User Id", example = "1000000000")
	private Long updatedBy;

	@QueryProjection
	public AdminUserResponseDto(
		Long id, String nickname, String email, String profileImage, Color profileColor, Long localeId,
		String localeCode,
		Long regionId, String regionCode, String regionAwsCode, String language, ApprovalStatus approvalStatus,
		MembershipStatus status, String authorityGroupName, ZonedDateTime createdDate, ZonedDateTime updatedDate,
		Long createdBy, Long updatedBy
	) {
		this.id = id;
		this.nickname = nickname;
		this.email = email;
		this.profileImage = profileImage;
		this.profileColor = profileColor;
		this.localeId = localeId;
		this.localeCode = localeCode;
		this.regionId = regionId;
		this.regionCode = regionCode;
		this.regionAwsCode = regionAwsCode;
		this.language = language;
		this.approvalStatus = approvalStatus;
		this.status = status;
		this.authorityGroupName = authorityGroupName;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}
}
