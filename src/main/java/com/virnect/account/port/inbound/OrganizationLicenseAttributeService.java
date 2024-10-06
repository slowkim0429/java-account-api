package com.virnect.account.port.inbound;

import java.util.List;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeRevisionResponseDto;
import com.virnect.account.domain.enumclass.LicenseAttributeType;

public interface OrganizationLicenseAttributeService {
	void createOrganizationLicenseAttribute(Long organizationLicenseId, Long licenseId);

	void createOrganizationLicenseAttribute(Long organizationLicenseId, Long licenseId, Long couponId);

	void unuseOrganizationLicenseAttribute(Long organizationLicenseId);

	List<OrganizationLicenseAttributeDetailResponseDto> getOrganizationLicenseAttributes(Long organizationLicenseId);

	List<OrganizationLicenseAttributeRevisionResponseDto> getRevisions(
		Long organizationLicenseId, LicenseAttributeType licenseAttributeType
	);
}
