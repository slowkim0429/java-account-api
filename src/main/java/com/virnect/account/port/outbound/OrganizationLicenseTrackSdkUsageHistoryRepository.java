package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.OrganizationLicenseTrackSdkUsageHistory;

public interface OrganizationLicenseTrackSdkUsageHistoryRepository extends
	JpaRepository<OrganizationLicenseTrackSdkUsageHistory, Long>,
	OrganizationLicenseTrackSdkUsageHistoryRepositoryCustom {
}
