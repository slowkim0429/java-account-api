package com.virnect.account.port.outbound;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseSearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseResponseDto;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.model.OrganizationLicense;

@Repository
public interface OrganizationLicenseRepositoryCustom {
	Optional<OrganizationLicense> getOrganizationLicense(Long id);

	Optional<OrganizationLicense> getOrganizationLicense(
		Long organizationId, Long contractId
	);

	List<OrganizationLicense> getOrganizationLicenses(
		Long organizationId, OrganizationLicenseStatus status, ProductType productType
	);

	Optional<OrganizationLicense> getOrganizationLicense(
		Long organizationId, OrganizationLicenseStatus organizationLicenseStatus, ProductType productType,
		LicenseGradeType licenseGradeType
	);

	Optional<OrganizationLicense> getOrganizationLicense(
		Long organizationId, OrganizationLicenseStatus organizationLicenseStatus, Boolean isExposed,
		ProductType productType, LicenseGradeType licenseGradeType
	);

	Page<OrganizationLicenseResponseDto> getOrganizationLicenseResponses(
		OrganizationLicenseSearchDto organizationLicenseSearchDto, Pageable pageable
	);

	Optional<OrganizationLicenseResponseDto> getOrganizationLicenseResponse(Long id);
}
