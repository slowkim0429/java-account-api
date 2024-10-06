package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.AcceptOrReject;
import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.User;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
public class UserRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "user id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "등록일시", example = "2022-01-03T11:15:30")
	private String createdDate;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03T11:15:30")
	private String updatedDate;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "이메일", example = "user@virnect.com")
	private String email;

	@ApiModelProperty(value = "허브스팟 아이디", example = "1000000000")
	private Long hubspotContactId;

	@ApiModelProperty(value = "언어", example = "en")
	private String language;

	@ApiModelProperty(value = "사용자 이용 지역 이름", example = "KR")
	private String localeCode;

	@ApiModelProperty(value = "사용자 이용 지역 id", example = "1000000000")
	private Long localeId;

	@ApiModelProperty(value = "광고 수신 동의 여부", example = "ACCEPT")
	private AcceptOrReject marketInfoReceive;

	@ApiModelProperty(value = "닉네임", example = "admin")
	private String nickname;

	@ApiModelProperty(value = "organization Id", example = "1000000000")
	private Long organizationId;

	@ApiModelProperty(value = "organization status", example = "1000000000")
	private UseStatus organizationStatus;

	@ApiModelProperty(value = "개인 정보 동의 여부", example = "ACCEPT")
	private AcceptOrReject privacyPolicy;

	@ApiModelProperty(value = "프로필 컬러", example = "BLUE")
	private Color profileColor;

	@ApiModelProperty(value = "organization status", example = "https://2022-09-27_qAUVyGAfdsfsdfdsCNWGnaGMMWfIZ.png")
	private String profileImage;

	@ApiModelProperty(value = "region aws code", example = "ap-northeast-2")
	private String serviceRegionAwsCode;

	@ApiModelProperty(value = "region code", example = "KR")
	private String serviceRegionCode;

	@ApiModelProperty(value = "region id", example = "1000000000")
	private Long serviceRegionId;

	@ApiModelProperty(value = "사용자 이용 가입 상태", example = "JOIN")
	private MembershipStatus status;

	@ApiModelProperty(value = "서비스 약관 동의 여부", example = "ACCEPT")
	private AcceptOrReject termsOfService;

	@ApiModelProperty(value = "사용자 유입 경로", example = "https://www.naver.com/")
	private String referrerUrl;

	@ApiModelProperty(value = "마지막 로그인 시간", example = "2022-01-04T11:44:55")
	private String lastLoginDate;

	@ApiModelProperty(value = "사용자의 zone id", example = "ROK")
	private String zoneId;

	public UserRevisionResponseDto(
		RevisionType revisionType, User user
	) {
		this.revisionType = revisionType;
		this.id = user.getId();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(user.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(user.getUpdatedDate());
		this.createdBy = user.getCreatedBy();
		this.updatedBy = user.getLastModifiedBy();
		this.email = user.getEmail();
		this.hubspotContactId = user.getHubSpotContactId();
		this.language = user.getLanguage();
		this.localeCode = user.getLocaleCode();
		this.localeId = user.getLocaleId();
		this.marketInfoReceive = user.getMarketInfoReceive();
		this.nickname = user.getNickname();
		this.organizationId = user.getOrganizationId();
		this.organizationStatus = user.getOrganizationStatus();
		this.privacyPolicy = user.getPrivacyPolicy();
		this.profileColor = user.getProfileColor();
		this.profileImage = user.getProfileImage();
		this.serviceRegionAwsCode = user.getRegionAwsCode();
		this.serviceRegionCode = user.getRegionCode();
		this.serviceRegionId = user.getRegionId();
		this.status = user.getStatus();
		this.termsOfService = user.getTermsOfService();
		this.referrerUrl = user.getReferrerUrl();
		this.lastLoginDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(user.getLastLoginDate());
		this.zoneId = user.getZoneId();
	}

	public static UserRevisionResponseDto of(Byte representation, User user) {
		return new UserRevisionResponseDto(RevisionType.valueOf(representation), user);
	}
}
