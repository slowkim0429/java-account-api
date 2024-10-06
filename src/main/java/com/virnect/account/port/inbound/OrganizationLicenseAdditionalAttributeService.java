package com.virnect.account.port.inbound;

import java.util.List;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeRevisionResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.model.OrganizationContract;

public interface OrganizationLicenseAdditionalAttributeService {
	void sync(OrganizationContract organizationContract);

	List<OrganizationLicenseAdditionalAttributeResponseDto> getOrganizationLicenseAdditionalAttributes(
		Long organizationLicenseId
	);

	List<OrganizationLicenseAdditionalAttributeRevisionResponseDto> getRevisions(
		Long organizationLicenseId, LicenseAdditionalAttributeType licenseAdditionalAttributeType
	);

}
