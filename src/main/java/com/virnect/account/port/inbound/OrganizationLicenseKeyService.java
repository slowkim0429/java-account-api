package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseKeySearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseKeyResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;

public interface OrganizationLicenseKeyService {

	void applyOrganizationLicenseKey();

	PageContentResponseDto<OrganizationLicenseKeyResponseDto> getOrganizationLicenseKeys(
		OrganizationLicenseKeySearchDto organizationLicenseKeySearchDto, Pageable pageable
	);

	void unuseOrganizationLicenseKey(Long organizationLicenseKeyId);
}
