package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeRevisionResponseDto;
import com.virnect.account.domain.enumclass.DependencyType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.LicenseAttribute;
import com.virnect.account.domain.model.OrganizationContract;
import com.virnect.account.domain.model.OrganizationLicense;
import com.virnect.account.domain.model.OrganizationLicenseAdditionalAttribute;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.OrganizationLicenseAdditionalAttributeService;
import com.virnect.account.port.inbound.OrganizationLicenseService;
import com.virnect.account.port.outbound.ItemRepository;
import com.virnect.account.port.outbound.LicenseAttributeRepository;
import com.virnect.account.port.outbound.OrganizationLicenseAdditionalAttributeRepository;
import com.virnect.account.port.outbound.OrganizationLicenseAdditionalAttributeRevisionRepository;
import com.virnect.account.port.outbound.OrganizationLicenseRepository;
import com.virnect.account.util.ZonedDateTimeUtil;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationLicenseAdditionalAttributeServiceImpl implements
	OrganizationLicenseAdditionalAttributeService {
	private final OrganizationLicenseRepository organizationLicenseRepository;
	private final OrganizationLicenseAdditionalAttributeRepository organizationLicenseAdditionalAttributeRepository;
	private final ItemRepository itemRepository;
	private final LicenseAttributeRepository licenseAttributeRepository;
	private final OrganizationLicenseAdditionalAttributeRevisionRepository organizationLicenseAdditionalAttributeRevisionRepository;

	private final OrganizationLicenseService organizationLicenseService;

	@Override
	public void sync(OrganizationContract organizationContract) {
		OrganizationLicense organizationLicense = getOrganizationLicenseInProgressByProductType(
			organizationContract.getOrganizationId(), ProductType.SQUARS);

		Item itemOfAttributeType = itemRepository.getItemById(organizationContract.getItemId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

		if (!organizationLicense.getLicenseId().equals(itemOfAttributeType.getLicenseId())) {
			throw new CustomException(NOT_FOUND_ORGANIZATION_LICENSE);
		}

		LicenseAttribute licenseAttribute = licenseAttributeRepository.getLicenseAttributeById(
				itemOfAttributeType.getLicenseAttributeId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_ATTRIBUTE));

		if (!licenseAttribute.getAttributeDependencyType().equals(DependencyType.INDEPENDENCE)) {
			throw new CustomException(INVALID_LICENSE_ATTRIBUTE);
		}

		organizationLicenseAdditionalAttributeRepository.getOrganizationLicenseAdditionalAttribute(
			organizationContract.getId()).ifPresentOrElse(organizationLicenseAdditionalAttribute -> {
			if (organizationContract.getStatus().isTermination()) {
				organizationLicenseAdditionalAttribute.setUnused();
			}
		}, () -> {
			OrganizationLicenseAdditionalAttribute organizationLicenseAdditionalAttribute = OrganizationLicenseAdditionalAttribute.of(
				organizationLicense.getId(), organizationContract.getId(),
				organizationContract.getContractId(), licenseAttribute
			);
			organizationLicenseAdditionalAttributeRepository.save(organizationLicenseAdditionalAttribute);
		});

		organizationLicenseService.sendOrganizationLicenseToWorkspaceAndSquars(
			organizationLicense, organizationContract.getRecurringInterval());
	}

	@Override
	public List<OrganizationLicenseAdditionalAttributeResponseDto> getOrganizationLicenseAdditionalAttributes(
		Long organizationLicenseId
	) {
		return organizationLicenseAdditionalAttributeRepository.getOrganizationLicenseAdditionalAttributeResponses(
			organizationLicenseId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganizationLicenseAdditionalAttributeRevisionResponseDto> getRevisions(
		Long organizationLicenseId, LicenseAdditionalAttributeType licenseAdditionalAttributeType
	) {
		return organizationLicenseAdditionalAttributeRevisionRepository.getRevisionResponses(
			organizationLicenseId, licenseAdditionalAttributeType);
	}

	private OrganizationLicense getOrganizationLicenseInProgressByProductType(
		Long organizationId, ProductType productType
	) {
		List<OrganizationLicense> processingOrganizationLicense = organizationLicenseRepository.getOrganizationLicenses(
			organizationId, OrganizationLicenseStatus.PROCESSING, productType);

		for (OrganizationLicense organizationLicense : processingOrganizationLicense) {
			if (isStarted(organizationLicense.getStartedAt()) && !isExpired(organizationLicense.getExpiredAt())) {
				return organizationLicense;
			}
		}

		throw new CustomException(NOT_FOUND_ORGANIZATION_LICENSE);
	}

	private boolean isExpired(ZonedDateTime expiredAt) {
		return ZonedDateTimeUtil.zoneOffsetOfUTC().isAfter(expiredAt);
	}

	private boolean isStarted(ZonedDateTime startedAt) {
		return ZonedDateTimeUtil.zoneOffsetOfUTC().isEqual(startedAt) ||
			ZonedDateTimeUtil.zoneOffsetOfUTC().isAfter(startedAt);
	}
}
