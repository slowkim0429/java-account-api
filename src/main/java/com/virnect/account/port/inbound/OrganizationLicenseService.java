package com.virnect.account.port.inbound;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseSearchDto;
import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseTrackSdkUsageSearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAndAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseDetailAndItemResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseTrackSdkUsageResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.outbound.request.OrganizationLicenseSendDto;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.model.OrganizationContract;
import com.virnect.account.domain.model.OrganizationLicense;

public interface OrganizationLicenseService {
	OrganizationLicense getOrganizationLicenseInProgressByProductType(Long organizationId, ProductType productType);

	List<OrganizationLicenseDetailAndItemResponseDto> getUsingOrganizationLicenseAndItem(Long organizationId);

	OrganizationLicenseAndAttributeResponseDto getMyOrganizationLicense();

	void sync(OrganizationContract organizationContract, Long contractRetainPeriodOfMonthWithAnnuallySubscription);

	void sync(Long organizationId, Long organizationLicenseId);

	void provideFreePlusOrganizationLicense(
		Long organizationId, ZonedDateTime startAt, ProductType productType
	);

	OrganizationLicense createFreePlusOrganizationLicense(
		Long organizationId, ZonedDateTime startAt, ProductType productType
	);

	OrganizationLicenseSendDto provideFreePlusOrganizationLicenseAtFirst(
		Long organizationId, ZonedDateTime startAt
	);

	PageContentResponseDto<OrganizationLicenseResponseDto> getOrganizationLicenses(
		OrganizationLicenseSearchDto organizationLicenseSearchDto, Pageable pageable
	);

	OrganizationLicenseResponseDto getOrganizationLicense(Long organizationLicenseId);

	void cancelFreePlusOrganizationLicense(Long organizationId, ZonedDateTime expiredAt);

	void sendOrganizationLicenseToWorkspaceAndSquars(
		OrganizationLicense organizationLicense, RecurringIntervalType contractRecurringInterval
	);

	List<OrganizationLicenseRevisionResponseDto> getOrganizationLicenseRevisions(Long organizationLicenseId);

	PageContentResponseDto<OrganizationLicenseTrackSdkUsageResponseDto> getTrackSdkUsageHistories(
		OrganizationLicenseTrackSdkUsageSearchDto organizationLicenseTrackSdkUsageSearchDto, PageRequest pageRequest
	);
}
