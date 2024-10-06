package com.virnect.account.domain.model;

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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Getter
@Audited
@Table(name = "organization_license_keys",
	indexes = {
		@Index(name = "idx_organization_id", columnList = "organization_id")
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationLicenseKey extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "organization_id", nullable = false)
	private Long organizationId;

	@Column(name = "organization_license_id", nullable = false)
	private Long organizationLicenseId;

	@Column(name = "email", length = 200, nullable = false)
	private String email;

	@Column(name = "license_key", length = 500)
	private String licenseKey;

	@Column(name = "use_status", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private UseStatus useStatus;

	private OrganizationLicenseKey(
		Long organizationId, Long organizationLicenseId, String email, String licenseKey, UseStatus useStatus
	) {
		this.organizationId = organizationId;
		this.organizationLicenseId = organizationLicenseId;
		this.email = email;
		this.licenseKey = licenseKey;
		this.useStatus = useStatus;
	}

	public static OrganizationLicenseKey create(
		Long organizationId, Long organizationLicenseId, String email, String licenseKey
	) {
		return new OrganizationLicenseKey(organizationId, organizationLicenseId, email, licenseKey, UseStatus.USE);
	}

	public void unuse() {
		this.useStatus = UseStatus.UNUSE;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}
}
