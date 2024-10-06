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

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Getter
@Audited
@Table(name = "organization_license_attributes",
	indexes = {
		@Index(name = "idx_organization_license_id", columnList = "organization_license_id"),
		@Index(name = "idx_status", columnList = "status")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationLicenseAttribute extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "organization_license_id", nullable = false)
	private Long organizationLicenseId;

	@Column(name = "license_attribute_id", nullable = false)
	private Long licenseAttributeId;

	@Column(name = "license_attribute_type", nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private LicenseAttributeType licenseAttributeType;

	@Column(name = "data_type", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private DataType dataType;

	@Column(name = "data_value", nullable = false, length = 10)
	private String dataValue;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private UseStatus status = UseStatus.USE;

	private OrganizationLicenseAttribute(
		Long organizationLicenseId, Long licenseAttributeId, LicenseAttributeType licenseAttributeType,
		DataType dataType, String dataValue
	) {

		this.organizationLicenseId = organizationLicenseId;
		this.licenseAttributeId = licenseAttributeId;
		this.licenseAttributeType = licenseAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
	}

	public static OrganizationLicenseAttribute of(
		Long organizationLicenseId, LicenseAttribute licenseAttribute, String dataValue
	) {
		return new OrganizationLicenseAttribute(
			organizationLicenseId, licenseAttribute.getId(), licenseAttribute.getAttributeType(),
			licenseAttribute.getDataType(), dataValue
		);
	}

	public void setUnused() {
		this.status = UseStatus.UNUSE;
	}

}
