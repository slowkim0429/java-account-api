package com.virnect.account.port.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseTrackSdkUsageRequestDto;
import com.virnect.account.domain.model.OrganizationLicenseTrackSdkUsageHistory;
import com.virnect.account.port.inbound.OrganizationLicenseTrackSdkUsageHistoryService;
import com.virnect.account.port.outbound.OrganizationLicenseTrackSdkUsageHistoryRepository;
import com.virnect.account.security.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationLicenseTrackSdkUsageHistoryServiceImpl implements
	OrganizationLicenseTrackSdkUsageHistoryService {

	private final OrganizationLicenseTrackSdkUsageHistoryRepository organizationLicenseTrackSdkUsageHistoryRepository;

	@Override
	public void createTrackSdkUsageHistory(
		OrganizationLicenseTrackSdkUsageRequestDto organizationLicenseTrackSdkUsageRequestDto
	) {
		Long organizationLicenseKeyId = SecurityUtil.getCurrentOrganizationLicenseKeyId();
		OrganizationLicenseTrackSdkUsageHistory organizationLicenseTrackSdkUsageHistory = OrganizationLicenseTrackSdkUsageHistory.of(
			organizationLicenseKeyId, organizationLicenseTrackSdkUsageRequestDto);
		organizationLicenseTrackSdkUsageHistoryRepository.save(organizationLicenseTrackSdkUsageHistory);
	}
}
