package com.virnect.account.port.outbound;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeRevisionResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;

@Repository
public interface OrganizationLicenseAdditionalAttributeRevisionRepository {
	List<OrganizationLicenseAdditionalAttributeRevisionResponseDto> getRevisionResponses(
		Long organizationLicenseId,
		LicenseAdditionalAttributeType licenseAdditionalAttributeType
	);
}
