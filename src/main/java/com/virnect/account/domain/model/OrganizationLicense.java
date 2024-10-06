package com.virnect.account.domain.model;

import java.time.ZonedDateTime;

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

import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.enumclass.ProductType;

@Entity
@Getter
@Audited
@Table(name = "organization_licenses",
	indexes = {
		@Index(name = "idx_organization_id", columnList = "organization_id"),
		@Index(name = "idx_organization_contract_id", columnList = "organization_contract_id"),
		@Index(name = "idx_license_grade_type", columnList = "license_grade_type"),
		@Index(name = "idx_item_id", columnList = "item_id"),
		@Index(name = "idx_license_id", columnList = "license_id"),
		@Index(name = "idx_status", columnList = "status")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationLicense extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "organization_id", nullable = false)
	private Long organizationId;

	@Column(name = "organization_contract_id", nullable = true)
	private Long organizationContractId;

	@Column(name = "contract_id", nullable = true)
	private Long contractId;

	@Column(name = "item_id", nullable = false)
	private Long itemId;

	@Column(name = "product_id", nullable = false)
	private Long productId;

	@Column(name = "product_name", nullable = false, length = 100)
	private String productName;

	@Column(name = "product_type", nullable = false, length = 100)
	@Enumerated(EnumType.STRING)
	private ProductType productType;

	@Column(name = "license_grade_id", nullable = false)
	private Long licenseGradeId;

	@Column(name = "license_grade_name", nullable = false, length = 50)
	private String licenseGradeName;

	@Column(name = "license_grade_type", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private LicenseGradeType licenseGradeType;

	@Column(name = "license_grade_description", nullable = false, length = 255)
	private String licenseGradeDescription;

	@Column(name = "license_id", nullable = false)
	private Long licenseId;

	@Column(name = "license_name", nullable = false, length = 50)
	private String licenseName;

	@Column(name = "license_description", nullable = false, length = 255)
	private String licenseDescription;

	@Column(name = "license_sales_target", nullable = false, length = 50)
	private String licenseSalesTarget;

	@Column(name = "started_at", nullable = false)
	private ZonedDateTime startedAt;

	@Column(name = "expired_at", nullable = false)
	private ZonedDateTime expiredAt;

	@Column(name = "status", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private OrganizationLicenseStatus status = OrganizationLicenseStatus.PROCESSING;

	private OrganizationLicense(
		Long organizationId, Long organizationContractId, Long contractId, Long itemId, Long productId,
		String productName,
		ProductType productType, Long licenseGradeId, String licenseGradeName, LicenseGradeType licenseGradeType,
		String licenseGradeDescription, Long licenseId, String licenseName, String licenseDescription,
		String licenseSalesTarget,
		ZonedDateTime startedAt, ZonedDateTime expiredAt
	) {
		this.organizationId = organizationId;
		this.organizationContractId = organizationContractId;
		this.contractId = contractId;
		this.itemId = itemId;
		this.productId = productId;
		this.productName = productName;
		this.productType = productType;
		this.licenseGradeId = licenseGradeId;
		this.licenseGradeName = licenseGradeName;
		this.licenseGradeType = licenseGradeType;
		this.licenseGradeDescription = licenseGradeDescription;
		this.licenseId = licenseId;
		this.licenseName = licenseName;
		this.licenseDescription = licenseDescription;
		this.licenseSalesTarget = licenseSalesTarget;
		this.startedAt = startedAt;
		this.expiredAt = expiredAt;
	}

	public static OrganizationLicense of(
		OrganizationContract organizationContract, Product product, LicenseGrade licenseGrade, License license
	) {
		return new OrganizationLicense(organizationContract.getOrganizationId(), organizationContract.getId(),
			organizationContract.getContractId(),
			organizationContract.getItemId(),
			product.getId(), product.getName(),
			product.getProductType(), licenseGrade.getId(), licenseGrade.getName(), licenseGrade.getGradeType(),
			licenseGrade.getDescription(),
			license.getId(), license.getName(), license.getDescription(), license.getSalesTarget(),
			organizationContract.getStartDate(),
			organizationContract.getEndDate()
		);
	}

	public static OrganizationLicense of(
		Long organizationId, Long itemId, ZonedDateTime startedAt, ZonedDateTime expiredAt, Product product,
		LicenseGrade licenseGrade, License license
	) {
		return new OrganizationLicense(organizationId, null, null, itemId,
			product.getId(), product.getName(),
			product.getProductType(), licenseGrade.getId(), licenseGrade.getName(), licenseGrade.getGradeType(),
			licenseGrade.getDescription(),
			license.getId(), license.getName(), license.getDescription(), license.getSalesTarget(), startedAt,
			expiredAt
		);
	}

	public void setStartedAt(ZonedDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public void setExpiredAt(ZonedDateTime expiredAt) {
		this.expiredAt = expiredAt;
	}

	public void setStatusCanceled() {
		this.status = OrganizationLicenseStatus.CANCELED;
	}

}
