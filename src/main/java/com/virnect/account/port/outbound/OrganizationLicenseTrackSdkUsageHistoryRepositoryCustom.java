package com.virnect.account.port.outbound;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseTrackSdkUsageSearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseTrackSdkUsageResponseDto;

public interface OrganizationLicenseTrackSdkUsageHistoryRepositoryCustom {
	Page<OrganizationLicenseTrackSdkUsageResponseDto> getTrackSdkHistoryResponses(
		OrganizationLicenseTrackSdkUsageSearchDto organizationLicenseTrackSdkUsageSearchDto, PageRequest pageable
	);
}
