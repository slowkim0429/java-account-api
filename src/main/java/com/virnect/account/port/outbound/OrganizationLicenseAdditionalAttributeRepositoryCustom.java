package com.virnect.account.port.outbound;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeResponseDto;
import com.virnect.account.domain.model.OrganizationLicenseAdditionalAttribute;

@Repository
public interface OrganizationLicenseAdditionalAttributeRepositoryCustom {
	Optional<OrganizationLicenseAdditionalAttribute> getOrganizationLicenseAdditionalAttribute(
		Long organizationContractId
	);

	List<OrganizationLicenseAdditionalAttribute> getOrganizationLicenseAdditionalAttributes(
		Long subscriptionOrganizationLicenseId
	);

	List<OrganizationLicenseAdditionalAttributeResponseDto> getOrganizationLicenseAdditionalAttributeResponses(
		Long subscriptionOrganizationLicenseId
	);
}
