package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseTrackSdkUsageRequestDto;

@Entity
@Getter
@Table(name = "organization_license_track_sdk_usage_histories",
	indexes = {
		@Index(name = "idx_content", columnList = "content"),
		@Index(name = "idx_organization_license_key_id", columnList = "organization_license_key_id")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationLicenseTrackSdkUsageHistory extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "organization_license_key_id", nullable = false)
	private Long organizationLicenseKeyId;

	@Column(name = "content", length = 100, nullable = false)
	private String content;

	private OrganizationLicenseTrackSdkUsageHistory(Long organizationLicenseKeyId, String content) {
		this.organizationLicenseKeyId = organizationLicenseKeyId;
		this.content = content;
	}

	public static OrganizationLicenseTrackSdkUsageHistory of(
		Long organizationLicenseKeyId,
		OrganizationLicenseTrackSdkUsageRequestDto organizationLicenseTrackSdkUsageRequestDto
	) {
		return new OrganizationLicenseTrackSdkUsageHistory(
			organizationLicenseKeyId, organizationLicenseTrackSdkUsageRequestDto.getContent());
	}
}
