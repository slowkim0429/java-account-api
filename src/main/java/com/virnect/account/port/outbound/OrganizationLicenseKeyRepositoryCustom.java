package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseKeySearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseKeyResponseDto;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.OrganizationLicenseKey;

public interface OrganizationLicenseKeyRepositoryCustom {
	Optional<OrganizationLicenseKey> getOrganizationLicenseKey(
		Long organizationId, Long organizationLicenseKeyId, UseStatus useStatus
	);

	Page<OrganizationLicenseKeyResponseDto> getOrganizationLicenseKeyResponses(
		OrganizationLicenseKeySearchDto organizationLicenseKeySearchDto, Pageable pageable
	);
}
