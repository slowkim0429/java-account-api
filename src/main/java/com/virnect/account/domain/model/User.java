package com.virnect.account.domain.model;

import java.time.ZonedDateTime;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.user.UserCreateRequestDto;
import com.virnect.account.domain.enumclass.AcceptOrReject;
import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@Entity
@Getter
@Audited
@Table(name = "users",
	indexes = {
		@Index(name = "idx_nickname", columnList = "nickname"),
		@Index(name = "idx_market_info_receive", columnList = "market_info_receive"),
		@Index(name = "idx_service_region_locale_mapping_id", columnList = "service_region_locale_mapping_id"),
		@Index(name = "idx_organization_status", columnList = "organization_status"),
		@Index(name = "idx_organization_id", columnList = "organization_id"),
		@Index(name = "idx_status", columnList = "status"),
		@Index(name = "idx_email", columnList = "email", unique = true),
		@Index(name = "idx_hubspot_contact_id", columnList = "hubspot_contact_id"),
		@Index(name = "idx_email_domain", columnList = "email_domain"),
		@Index(name = "idx_created_at", columnList = "created_at"),
		@Index(name = "idx_updated_at", columnList = "updated_at"),
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nickname", length = 30, nullable = false)
	private String nickname;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "email_domain", length = 50)
	private String emailDomain;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "password", length = 100, nullable = false)
	private String password;

	@Column(name = "profile_image")
	private String profileImage;

	@Column(name = "profile_color", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private Color profileColor;

	@Column(name = "market_info_receive", length = 50, nullable = false)
	@Enumerated(EnumType.STRING)
	private AcceptOrReject marketInfoReceive;

	@Column(name = "privacy_policy", length = 50, nullable = false)
	@Enumerated(EnumType.STRING)
	private AcceptOrReject privacyPolicy;

	@Column(name = "terms_of_service", length = 50, nullable = false)
	@Enumerated(EnumType.STRING)
	private AcceptOrReject termsOfService;

	@Column(name = "zone_id", nullable = false, length = 50)
	private String zoneId;

	@Column(name = "service_region_locale_mapping_id", nullable = false)
	private Long localeId;

	@Column(name = "service_region_locale_mapping_code", nullable = false)
	private String localeCode;

	@Column(name = "service_region_id", nullable = false)
	private Long regionId;

	@Column(name = "service_region_code", nullable = false)
	private String regionCode;

	@Column(name = "service_region_aws_code", nullable = false)
	private String regionAwsCode;

	@Column(name = "language", nullable = false, length = 10)
	private String language;

	@Column(name = "referrer_url")
	private String referrerUrl;

	@Column(name = "organization_id")
	private Long organizationId;

	@Column(name = "organization_status", length = 50)
	@Enumerated(EnumType.STRING)
	private UseStatus organizationStatus;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private MembershipStatus status;

	@Column(name = "hubspot_contact_id")
	private Long hubSpotContactId;

	@Column(name = "last_login_date")
	private ZonedDateTime lastLoginDate;

	@Builder(builderClassName = "SignUpBuilder", builderMethodName = "signUpBuilder")
	public User(
		UserCreateRequestDto requestDto, String nickname, String encodedPassword, ServiceRegionLocaleMapping userLocale,
		ServiceRegion userRegion, String zoneId
	) {
		this.email = requestDto.getLowerCaseEmail();
		this.emailDomain = this.email.split("@")[1];
		this.nickname = nickname;
		this.password = encodedPassword;
		this.profileColor = Color.getRandomColor();
		this.marketInfoReceive = requestDto.marketInfoReceiveValueOf();
		this.privacyPolicy = requestDto.privacyPolicyValueOf();
		this.termsOfService = requestDto.termsOfServiceValueOf();
		this.zoneId = zoneId;
		this.localeId = userLocale.getId();
		this.localeCode = userLocale.getCode();
		this.regionId = userRegion.getId();
		this.regionCode = userRegion.getCode();
		this.regionAwsCode = userRegion.getAwsCode();
		this.language = Locale.ENGLISH.toString();
		this.referrerUrl = requestDto.getReferrerUrl();
		this.organizationStatus = UseStatus.NONE;
		this.status = MembershipStatus.JOIN;
	}

	public void delete() {
		this.email = null;
		this.emailDomain = "";
		this.nickname = "";
		this.password = "";
		this.profileImage = "";
		this.marketInfoReceive = AcceptOrReject.REJECT;
		this.localeCode = "";
		this.regionCode = "";
		this.regionAwsCode = "";
		this.language = "";
		this.organizationStatus = UseStatus.DELETE;
		this.status = MembershipStatus.RESIGN;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public void setOrganizationUse(UseStatus useStatus) {
		this.organizationStatus = useStatus;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updateMarketInfoReceive(AcceptOrReject marketInfoReceive) {
		this.marketInfoReceive = marketInfoReceive;
	}

	public void updateLocale(Long localeId, String localeCode, Long regionId, String regionCode, String regionAwsCode) {
		this.localeId = localeId;
		this.localeCode = localeCode;
		this.regionId = regionId;
		this.regionCode = regionCode;
		this.regionAwsCode = regionAwsCode;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public void setHubSpotContactId(Long hubSpotContactId) {
		this.hubSpotContactId = hubSpotContactId;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLastLoginDate() {
		this.lastLoginDate = ZonedDateTimeUtil.zoneOffsetOfUTC();
	}

	public void updateZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
}
