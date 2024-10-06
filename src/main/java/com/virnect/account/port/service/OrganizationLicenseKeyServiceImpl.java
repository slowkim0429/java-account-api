package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseKeySearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseKeyResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.domain.model.OrganizationLicense;
import com.virnect.account.domain.model.OrganizationLicenseKey;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.EmailAuthService;
import com.virnect.account.port.inbound.OrganizationLicenseKeyService;
import com.virnect.account.port.inbound.OrganizationLicenseService;
import com.virnect.account.port.outbound.OrganizationLicenseKeyRepository;
import com.virnect.account.port.outbound.OrganizationLicenseRepository;
import com.virnect.account.port.outbound.OrganizationRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.security.jwt.TokenProvider;
import com.virnect.account.util.ZonedDateTimeUtil;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationLicenseKeyServiceImpl implements OrganizationLicenseKeyService {
	private final OrganizationLicenseService organizationLicenseService;
	private final EmailAuthService emailAuthService;

	private final OrganizationLicenseKeyRepository organizationLicenseKeyRepository;
	private final OrganizationRepository organizationRepository;
	private final OrganizationLicenseRepository organizationLicenseRepository;

	private final TokenProvider tokenProvider;

	@Override
	public void applyOrganizationLicenseKey() {
		Long currentOrganizationId = SecurityUtil.getCurrentUserOrganizationId();
		Long currentUserId = SecurityUtil.getCurrentUserId();
		Organization organization = organizationRepository.getOrganization(currentOrganizationId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION));
		String organizationEmail = organization.getEmail();

		organizationLicenseKeyRepository.getOrganizationLicenseKey(currentOrganizationId, null, UseStatus.USE)
			.ifPresentOrElse(organizationLicenseKey -> {
				cancelOrganizationLicense(organizationLicenseKey.getOrganizationLicenseId());
				organizationLicenseKey.unuse();

				OrganizationLicense newOrganizationLicense = organizationLicenseService.createFreePlusOrganizationLicense(
					currentOrganizationId, ZonedDateTimeUtil.zoneOffsetOfUTC(), ProductType.TRACK);

				OrganizationLicenseKey newOrganizationLicenseKey = OrganizationLicenseKey.create(
					currentOrganizationId, newOrganizationLicense.getId(), organizationEmail,
					null
				);
				organizationLicenseKeyRepository.save(newOrganizationLicenseKey);

				String licenseKey = tokenProvider.createTrackToken(
					currentUserId,
					newOrganizationLicenseKey.getId()
				);

				newOrganizationLicenseKey.setLicenseKey(licenseKey);
				organizationLicenseKeyRepository.save(newOrganizationLicenseKey);

				emailAuthService.sendOrganizationLicenseKeyEmail(
					organizationEmail, licenseKey);
			}, () -> {
				OrganizationLicense organizationLicense = organizationLicenseService.createFreePlusOrganizationLicense(
					currentOrganizationId, ZonedDateTimeUtil.zoneOffsetOfUTC(), ProductType.TRACK);

				OrganizationLicenseKey newOrganizationLicenseKey = OrganizationLicenseKey.create(
					currentOrganizationId, organizationLicense.getId(), organizationEmail, null);

				organizationLicenseKeyRepository.save(newOrganizationLicenseKey);

				String licenseKey = tokenProvider.createTrackToken(
					currentUserId,
					newOrganizationLicenseKey.getId()
				);

				newOrganizationLicenseKey.setLicenseKey(licenseKey);
				organizationLicenseKeyRepository.save(newOrganizationLicenseKey);

				emailAuthService.sendOrganizationLicenseKeyEmail(organizationEmail, licenseKey);
			});
	}

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<OrganizationLicenseKeyResponseDto> getOrganizationLicenseKeys(
		OrganizationLicenseKeySearchDto organizationLicenseKeySearchDto, Pageable pageable
	) {
		Page<OrganizationLicenseKeyResponseDto> organizationLicenseKeyResponses = organizationLicenseKeyRepository.getOrganizationLicenseKeyResponses(
			organizationLicenseKeySearchDto, pageable);
		return new PageContentResponseDto<>(organizationLicenseKeyResponses, pageable);
	}

	@Override
	public void unuseOrganizationLicenseKey(Long organizationLicenseKeyId) {
		OrganizationLicenseKey organizationLicenseKey = organizationLicenseKeyRepository.getOrganizationLicenseKey(
				null, organizationLicenseKeyId, null)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION_LICENSE_KEY));

		if (organizationLicenseKey.getUseStatus().isNotUse()) {
			throw new CustomException(INVALID_STATUS);
		}

		organizationLicenseKey.unuse();
		cancelOrganizationLicense(organizationLicenseKey.getOrganizationLicenseId());
	}

	private void cancelOrganizationLicense(Long organizationLicenseId) {
		OrganizationLicense organizationLicense = organizationLicenseRepository.getOrganizationLicense(
				organizationLicenseId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION_LICENSE));

		organizationLicense.setExpiredAt(ZonedDateTimeUtil.zoneOffsetOfUTC());
		organizationLicense.setStatusCanceled();
		organizationLicenseRepository.save(organizationLicense);
	}
}
