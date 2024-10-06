package com.virnect.account.port.outbound;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseRevisionResponseDto;

@Repository
public interface OrganizationLicenseRevisionRepository {
	List<OrganizationLicenseRevisionResponseDto> getOrganizationLicenseRevisionResponses(Long organizationLicenseId);
}
