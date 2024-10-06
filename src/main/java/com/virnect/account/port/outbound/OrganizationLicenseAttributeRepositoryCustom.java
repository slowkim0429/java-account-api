package com.virnect.account.port.outbound;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeResponseDto;
import com.virnect.account.domain.model.OrganizationLicenseAttribute;

@Repository
public interface OrganizationLicenseAttributeRepositoryCustom {
	List<OrganizationLicenseAttribute> getOrganizationLicenseAttributes(Long organizationLicenseId);

	List<OrganizationLicenseAttributeResponseDto> getOrganizationLicenseAttributeResponses(Long organizationLicenseId);

	List<OrganizationLicenseAttributeDetailResponseDto> getOrganizationLicenseAttributeDetailResponses(
		Long organizationLicenseId
	);
}
