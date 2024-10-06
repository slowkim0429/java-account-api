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
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Getter
@Audited
@Table(name = "organization_license_additional_attributes",
	indexes = {
		@Index(name = "idx_subscription_organization_license_id", columnList = "subscription_organization_license_id"),
		@Index(name = "idx_organization_contract_id", columnList = "organization_contract_id"),
		@Index(name = "idx_contract_id", columnList = "contract_id"),
		@Index(name = "idx_status", columnList = "status")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationLicenseAdditionalAttribute extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "subscription_organization_license_id", nullable = false)
	private Long subscriptionOrganizationLicenseId;

	@Column(name = "organization_contract_id", nullable = false)
	private Long organizationContractId;

	@Column(name = "contract_id", nullable = false)
	private Long contractId;

	@Column(name = "license_attribute_id", nullable = false)
	private Long licenseAttributeId;

	@Column(name = "license_additional_attribute_type", nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private LicenseAdditionalAttributeType licenseAdditionalAttributeType;

	@Column(name = "data_type", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private DataType dataType;

	@Column(name = "data_value", nullable = false, length = 10)
	private String dataValue;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private UseStatus status = UseStatus.USE;

	private OrganizationLicenseAdditionalAttribute(
		Long subscriptionOrganizationLicenseId, Long organizationContractId, Long contractId, Long licenseAttributeId,
		LicenseAdditionalAttributeType licenseAdditionalAttributeType, DataType dataType, String dataValue
	) {
		this.subscriptionOrganizationLicenseId = subscriptionOrganizationLicenseId;
		this.organizationContractId = organizationContractId;
		this.contractId = contractId;
		this.licenseAttributeId = licenseAttributeId;
		this.licenseAdditionalAttributeType = licenseAdditionalAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
	}

	public static OrganizationLicenseAdditionalAttribute of(
		Long organizationLicenseId, Long organizationContractId, Long contractId, LicenseAttribute licenseAttribute
	) {
		return new OrganizationLicenseAdditionalAttribute(
			organizationLicenseId, organizationContractId, contractId, licenseAttribute.getId(),
			licenseAttribute.getAdditionalAttributeType(), licenseAttribute.getDataType(),
			licenseAttribute.getDataValue()
		);
	}

	public void setUnused() {
		this.status = UseStatus.UNUSE;
	}

}
