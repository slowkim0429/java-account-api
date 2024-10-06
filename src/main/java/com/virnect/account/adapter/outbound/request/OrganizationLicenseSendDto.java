package com.virnect.account.adapter.outbound.request;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.OrganizationLicense;

@Getter
@NoArgsConstructor
public class OrganizationLicenseSendDto {
	private Long organizationId;
	private Long organizationLicenseId;
	private Long contractId;
	private OrganizationLicenseStatus status;
	private ZonedDateTime startAt;
	private ZonedDateTime expiredAt;
	private RecurringIntervalType recurringIntervalType;
	private Long licenseGradeId;
	private String licenseGradeName;
	private LicenseGradeType licenseGradeType;
	private UseStatus licenseGradeStatus;
	private Long licenseId;
	private String licenseName;
	private UseStatus licenseStatus;
	private Long productId;
	private String productName;
	private ProductType productType;
	private UseStatus productStatus;
	private List<OrganizationLicenseAttributeSendDto> attributes;
	private List<OrganizationLicenseAdditionalAttributeSendDto> additionalAttributes;
	private Long lastModifiedBy;

	@Builder
	public OrganizationLicenseSendDto(
		OrganizationLicense organizationLicense, RecurringIntervalType recurringIntervalType,
		List<OrganizationLicenseAttributeSendDto> attributes,
		List<OrganizationLicenseAdditionalAttributeSendDto> additionalAttributes

	) {
		this.organizationId = organizationLicense.getOrganizationId();
		this.organizationLicenseId = organizationLicense.getId();
		this.contractId = organizationLicense.getContractId();
		this.status = organizationLicense.getStatus();
		this.startAt = organizationLicense.getStartedAt();
		this.expiredAt = organizationLicense.getExpiredAt();
		this.recurringIntervalType =
			organizationLicense.getLicenseGradeType().isFree() ? RecurringIntervalType.NONE : recurringIntervalType;
		this.licenseGradeId = organizationLicense.getLicenseGradeId();
		this.licenseGradeName = organizationLicense.getLicenseGradeName();
		this.licenseGradeType = organizationLicense.getLicenseGradeType();
		this.licenseId = organizationLicense.getLicenseId();
		this.licenseName = organizationLicense.getLicenseName();
		this.productId = organizationLicense.getProductId();
		this.productName = organizationLicense.getProductName();
		this.productType = organizationLicense.getProductType();
		this.attributes = attributes;
		this.additionalAttributes = additionalAttributes;
		this.lastModifiedBy = organizationLicense.getLastModifiedBy();
	}

}
