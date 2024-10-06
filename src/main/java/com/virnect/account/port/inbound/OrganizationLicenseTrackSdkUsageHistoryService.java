package com.virnect.account.port.inbound;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseTrackSdkUsageRequestDto;

public interface OrganizationLicenseTrackSdkUsageHistoryService {

	void createTrackSdkUsageHistory(
		OrganizationLicenseTrackSdkUsageRequestDto organizationLicenseTrackSdkUsageRequestDto
	);
}
