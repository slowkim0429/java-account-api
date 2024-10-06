package com.virnect.account.domain.model;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.user.AdminUserRequestDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.Color;
import com.virnect.account.domain.enumclass.MembershipStatus;

@Entity
@Getter
@Audited
@Table(name = "admin_users",
	indexes = {
		@Index(name = "idx_nickname", columnList = "nickname"),
		@Index(name = "idx_service_region_locale_mapping_id", columnList = "service_region_locale_mapping_id"),
		@Index(name = "idx_approval_status", columnList = "approval_status"),
		@Index(name = "idx_status", columnList = "status")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUser extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nickname", length = 30, nullable = false)
	private String nickname;

	@Column(name = "email", length = 100, unique = true)
	private String email;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "password", length = 100, nullable = false)
	private String password;

	@Column(name = "profile_image")
	private String profileImage;

	@Column(name = "profile_color", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private Color profileColor;

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

	@Column(name = "approval_status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private ApprovalStatus approvalStatus;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private MembershipStatus status;

	@Setter
	@Column(name = "authority_group_id", nullable = true)
	private Long authorityGroupId;

	public static AdminUser of(
		AdminUserRequestDto adminUserRequestDto, String nickname, String encodedPassword,
		ServiceRegionLocaleMapping userLocale,
		ServiceRegion userRegion
	) {
		AdminUser adminUser = new AdminUser();
		adminUser.email = adminUserRequestDto.getLowerCaseEmail();
		adminUser.nickname = nickname;
		adminUser.password = encodedPassword;
		adminUser.profileColor = Color.getRandomColor();
		adminUser.localeId = userLocale.getId();
		adminUser.localeCode = userLocale.getCode();
		adminUser.regionId = userRegion.getId();
		adminUser.regionCode = userRegion.getCode();
		adminUser.regionAwsCode = userRegion.getAwsCode();
		adminUser.language = Locale.ENGLISH.toString();
		adminUser.approvalStatus = ApprovalStatus.REGISTER;
		adminUser.status = MembershipStatus.JOIN;
		return adminUser;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public void delete() {
		this.status = MembershipStatus.RESIGN;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public boolean isAvailableStatus() {
		if (this.status.isResigned()) {
			return false;
		}
		if (this.approvalStatus.isNotApproved()) {
			return false;
		}
		return true;
	}
}
