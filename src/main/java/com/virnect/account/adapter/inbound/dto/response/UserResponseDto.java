package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import com.virnect.account.domain.enumclass.AcceptOrReject;
import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.User;
import com.virnect.account.domain.model.UserRole;
import com.virnect.account.security.service.CustomUserDetails;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
public class UserResponseDto {
	@ApiModelProperty(value = "user id", example = "1000000000")
	private final Long id;

	@ApiModelProperty(value = "닉네임", example = "돌 굴러가유")
	private final String nickname;

	@ApiModelProperty(value = "이메일", example = "test@virnect.com")
	private final String email;

	@ApiModelProperty(value = "프로필 이미지 url", example = "default")
	private final String profileImage;

	@ApiModelProperty(value = "프로필 color", example = "GREEN")
	private final Color profileColor;

	@ApiModelProperty(value = "광고 수신 동의 여부", example = "REJECT")
	private final AcceptOrReject marketInfoReceive;

	@ApiModelProperty(value = "언어 정보", example = "ko_KR")
	private final String language;

	@ApiModelProperty(value = "account id", example = "1000000000")
	private final Long organizationId;

	@ApiModelProperty(value = "account 등록 상태", example = "USE")
	private final UseStatus accountStatus;

	@ApiModelProperty(value = "정보 생성일자", example = "2022-01-04T11:44:55")
	private final String createdDate;

	@ApiModelProperty(value = "정보 수정일자", example = "2022-01-04T11:44:55")
	private final String updatedDate;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "권한 정보")
	private List<RoleResponseDto> roles;

	@ApiModelProperty(value = "사용자 이용 지역 코드", example = "KR")
	private String localeCode;

	@ApiModelProperty(value = "사용자 이용 지역 이름", example = "Korea, Republic of")
	private String localeName = "";

	@ApiModelProperty(value = "사용자 이용 지역 id", example = "1000000000")
	private Long localeId;

	@ApiModelProperty(value = "사용자 이용 가입 상태", example = "JOIN")
	private MembershipStatus status;

	@ApiModelProperty(value = "허브스팟 아이디", example = "1000000000")
	private Long hubspotContactId;

	@ApiModelProperty(value = "개인 정보 동의 여부", example = "ACCEPT")
	private AcceptOrReject privacyPolicy;

	@ApiModelProperty(value = "region aws code", example = "ap-northeast-2")
	private String serviceRegionAwsCode;

	@ApiModelProperty(value = "region code", example = "KR")
	private String serviceRegionCode;

	@ApiModelProperty(value = "region id", example = "1000000000")
	private Long serviceRegionId;

	@ApiModelProperty(value = "서비스 약관 동의 여부", example = "ACCEPT")
	private AcceptOrReject termsOfService;

	@ApiModelProperty(value = "사용자 유입 경로", example = "https://www.naver.com/")
	private String referrerUrl;

	@ApiModelProperty(value = "마지막 로그인 시간", example = "2022-01-04T11:44:55")
	private String lastLoginDate;

	@ApiModelProperty(value = "사용자의 zone id", example = "ROK")
	private String zoneId;

	@QueryProjection
	public UserResponseDto(
		Long id, String nickname, String email, String profileImage, Color profileColor,
		AcceptOrReject marketInfoReceive, String language, Long organizationId, UseStatus accountStatus,
		ZonedDateTime createdDate, ZonedDateTime updatedDate, Long createdBy, Long updatedBy, String localeName,
		Long localeId, MembershipStatus status, Long hubspotContactId, AcceptOrReject privacyPolicy,
		String serviceRegionAwsCode, String serviceRegionCode, Long serviceRegionId, AcceptOrReject termsOfService,
		String referrerUrl, ZonedDateTime lastLoginDate, String zoneId
	) {
		this.id = id;
		this.nickname = nickname;
		this.email = email;
		this.profileImage = profileImage;
		this.profileColor = profileColor;
		this.marketInfoReceive = marketInfoReceive;
		this.language = language;
		this.organizationId = organizationId;
		this.accountStatus = accountStatus;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.localeName = localeName;
		this.localeId = localeId;
		this.status = status;
		this.hubspotContactId = hubspotContactId;
		this.privacyPolicy = privacyPolicy;
		this.serviceRegionAwsCode = serviceRegionAwsCode;
		this.serviceRegionCode = serviceRegionCode;
		this.serviceRegionId = serviceRegionId;
		this.termsOfService = termsOfService;
		this.referrerUrl = referrerUrl;
		this.lastLoginDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(lastLoginDate);
		this.zoneId = zoneId;
	}

	@Builder
	public UserResponseDto(User user) {
		this.id = user.getId();
		this.nickname = user.getNickname();
		this.email = user.getEmail();
		this.profileImage = user.getProfileImage();
		this.profileColor = user.getProfileColor();
		this.marketInfoReceive = user.getMarketInfoReceive();
		this.language = user.getLanguage();
		this.organizationId = user.getOrganizationId();
		this.accountStatus = user.getOrganizationStatus();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(user.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(user.getUpdatedDate());
		this.localeName = user.getLocaleCode();
		this.status = user.getStatus();
	}

	@Builder(builderClassName = "UserCustomUserDetailsBuilder", builderMethodName = "userCustomUserDetailsBuilder")
	public UserResponseDto(CustomUserDetails customUserDetails) {
		this.id = customUserDetails.getId();
		this.nickname = customUserDetails.getNickname();
		this.email = customUserDetails.getEmail();
		this.profileImage = customUserDetails.getProfile();
		this.profileColor = customUserDetails.getProfileColor();
		this.marketInfoReceive = customUserDetails.getMarketInfoReceive();
		this.language = customUserDetails.getLanguage();
		this.organizationId = customUserDetails.getOrganizationId();
		this.accountStatus = customUserDetails.getOrganizationStatus();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(customUserDetails.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(customUserDetails.getUpdateDate());
		this.localeId = customUserDetails.getLocaleId();
		this.localeCode = customUserDetails.getLocaleCode();
		this.zoneId = customUserDetails.getZoneId();
	}

	public void setUserRoles(List<UserRole> roles) {
		this.roles = roles.stream()
			.map(userRole -> new RoleResponseDto(
				userRole.getRole(), ZonedDateTimeUtil.convertToStringWithCurrentZoneId(userRole.getCreatedDate())))
			.collect(Collectors.toList());
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}
}
