package com.virnect.account.port.outbound;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeRevisionResponseDto;
import com.virnect.account.domain.enumclass.LicenseAttributeType;

@Repository
public interface OrganizationLicenseAttributeRevisionRepository {
	List<OrganizationLicenseAttributeRevisionResponseDto> getRevisionResponses(
		Long organizationLicenseId, LicenseAttributeType licenseAttributeType
	);
}
