package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.hubspot.ExternalServiceMappingSearchDto;
import com.virnect.account.adapter.inbound.dto.request.hubspot.ExternalServiceMappingSyncableRequestDto;
import com.virnect.account.adapter.inbound.dto.response.ExternalServiceMappingRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.HubspotMappingResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.ExternalDomain;
import com.virnect.account.domain.enumclass.InternalDomain;
import com.virnect.account.domain.model.ExternalServiceMapping;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.domain.model.User;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.ExternalServiceMappingService;
import com.virnect.account.port.inbound.HubspotService;
import com.virnect.account.port.outbound.ExternalServiceMappingRepository;
import com.virnect.account.port.outbound.ExternalServiceMappingRevisionRepository;
import com.virnect.account.port.outbound.ItemRepository;
import com.virnect.account.port.outbound.OrganizationRepository;
import com.virnect.account.port.outbound.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ExternalServiceMappingServiceImpl implements ExternalServiceMappingService {
	private final HubspotService hubSpotService;
	private final ExternalServiceMappingRepository externalServiceMappingRepository;
	private final UserRepository userRepository;
	private final OrganizationRepository organizationRepository;
	private final ItemRepository itemRepository;
	private final ExternalServiceMappingRevisionRepository externalServiceMappingRevisionRepository;

	@Override
	public void register(User newUser, Organization newOrganization) {
		boolean existContact = existContactByEmail(newUser);

		if (existContact) {
			this.updateUser(newUser);
			return;
		}

		this.createUser(newUser);
		this.createOrganization(newOrganization, newUser.getHubSpotContactId());
	}

	public void createOrganization(Organization newOrganization, Long hubSpotContactId) {
		ExternalServiceMapping externalServiceMapping = getExternalMappingByOrganizationId(
			newOrganization.getId());
		externalServiceMappingRepository.save(externalServiceMapping);

		try {
			hubSpotService.createCompany(newOrganization);
			hubSpotService.associateContactToCompany(hubSpotContactId, newOrganization.getHubspotCompanyId());
			externalServiceMapping.setExternalMappingId(newOrganization.getHubspotCompanyId());
			externalServiceMapping.setLatestMappingSucceeded(true);
		} catch (Exception e) {
			externalServiceMapping.setLatestMappingSucceeded(false);
		}
	}

	public void createUser(User newUser) {
		ExternalServiceMapping externalServiceMapping = getExternalMappingByUserId(newUser.getId());

		try {
			hubSpotService.createContact(newUser);
			externalServiceMapping.setExternalMappingId(newUser.getHubSpotContactId());
			externalServiceMapping.setLatestMappingSucceeded(true);
		} catch (Exception e) {
			externalServiceMapping.setLatestMappingSucceeded(false);
		}
	}

	private boolean existContactByEmail(User user) {
		try {
			return hubSpotService.existContactByEmail(user);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void createItem(Item item) {
		ExternalServiceMapping externalServiceMapping = new ExternalServiceMapping(
			ExternalDomain.PRODUCT, InternalDomain.ITEM, item.getId(), false);
		externalServiceMappingRepository.save(externalServiceMapping);

		try {
			hubSpotService.createProduct(item);
			externalServiceMapping.setExternalMappingId(item.getHubSpotProductId());
			externalServiceMapping.setLatestMappingSucceeded(true);
		} catch (Exception e) {
			externalServiceMapping.setLatestMappingSucceeded(false);
		}
	}

	@Override
	public void updateOrganization(Organization organization) {
		ExternalServiceMapping externalServiceMapping = getExternalMappingByOrganizationId(organization.getId());

		try {
			hubSpotService.updateCompany(organization);
			externalServiceMapping.setExternalMappingId(organization.getHubspotCompanyId());
			externalServiceMapping.setLatestMappingSucceeded(true);
		} catch (Exception e) {
			externalServiceMapping.setLatestMappingSucceeded(false);
		}

	}

	public ExternalServiceMapping getExternalMappingByOrganizationId(Long organizationId) {
		return externalServiceMappingRepository.getExternalServiceMapping(
				null, InternalDomain.ORGANIZATION, organizationId)
			.orElseGet(() -> {
					ExternalServiceMapping externalServiceMapping = new ExternalServiceMapping(ExternalDomain.COMPANY,
						InternalDomain.ORGANIZATION, organizationId, false
					);
					externalServiceMappingRepository.save(externalServiceMapping);
					return externalServiceMapping;
				}
			);
	}

	@Override
	public void updateLoginDate(Long hubspotContactId, LocalDate loginDate) {
		try {
			hubSpotService.updateLoginDate(hubspotContactId, loginDate);
		} catch (Exception ignore) {
		}
	}

	@Override
	public void updateUser(User user) {
		ExternalServiceMapping externalServiceMapping = getExternalMappingByUserId(
			user.getId());

		try {
			hubSpotService.updateContact(user);
			externalServiceMapping.setExternalMappingId(user.getHubSpotContactId());
			externalServiceMapping.setLatestMappingSucceeded(true);
		} catch (Exception e) {
			externalServiceMapping.setLatestMappingSucceeded(false);
		}
	}

	@Override
	public PageContentResponseDto<HubspotMappingResponseDto> getHubspotServiceMappings(
		ExternalServiceMappingSearchDto externalServiceMappingSearchDto, Pageable pageable
	) {
		Page<HubspotMappingResponseDto> hubspotMappingResponses = externalServiceMappingRepository.getExternalServiceMappingResponses(
			externalServiceMappingSearchDto,
			pageable
		);
		return new PageContentResponseDto<>(hubspotMappingResponses, pageable);
	}

	@Override
	public void synchronizeUserAndHubspotMapping(Long userId) {
		User user = userRepository.getUser(userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

		Optional<ExternalServiceMapping> optionalExternalServiceMapping = externalServiceMappingRepository.getExternalServiceMapping(
			null, InternalDomain.USER, userId);

		if (optionalExternalServiceMapping.isEmpty()) {
			return;
		}
		ExternalServiceMapping externalServiceMapping = optionalExternalServiceMapping.get();

		if (externalServiceMapping.getIsLatestMappingSucceeded()) {
			return;
		}

		if (!externalServiceMapping.getIsSyncable()) {
			throw new CustomException(INVALID_SYNCABLE);
		}

		synchronizeUser(user, externalServiceMapping);
	}

	@Override
	public void synchronizeOrganizationAndHubspotMapping(Long organizationId) {
		Organization organization = organizationRepository.getOrganization(organizationId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORGANIZATION));

		Optional<ExternalServiceMapping> optionalExternalServiceMapping = externalServiceMappingRepository.getExternalServiceMapping(
			null, InternalDomain.ORGANIZATION, organizationId);

		if (optionalExternalServiceMapping.isEmpty()) {
			return;
		}

		ExternalServiceMapping externalServiceMapping = optionalExternalServiceMapping.get();

		if (externalServiceMapping.getIsLatestMappingSucceeded()) {
			return;
		}

		if (!externalServiceMapping.getIsSyncable()) {
			throw new CustomException(INVALID_SYNCABLE);
		}

		synchronizeOrganization(organization, externalServiceMapping);
	}

	@Override
	public void synchronizeItemAndHubspotMapping(Long itemId) {
		Item item = itemRepository.getItemById(itemId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ITEM));

		Optional<ExternalServiceMapping> optionalExternalServiceMapping = externalServiceMappingRepository.getExternalServiceMapping(
			null, InternalDomain.ITEM, itemId);

		if (optionalExternalServiceMapping.isEmpty()) {
			return;
		}

		ExternalServiceMapping externalServiceMapping = optionalExternalServiceMapping.get();

		if (externalServiceMapping.getIsLatestMappingSucceeded()) {
			return;
		}

		if (!externalServiceMapping.getIsSyncable()) {
			throw new CustomException(INVALID_SYNCABLE);
		}

		synchronizeItem(item, externalServiceMapping);
	}

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<ExternalServiceMappingRevisionResponseDto> getRevisions(
		Long externalServiceMappingId, Pageable pageable
	) {
		Page<ExternalServiceMappingRevisionResponseDto> externalServiceMappingRevisionResponseDtos =
			externalServiceMappingRevisionRepository.getRevisions(externalServiceMappingId, pageable);
		return new PageContentResponseDto<>(externalServiceMappingRevisionResponseDtos, pageable);
	}

	private void synchronizeItem(Item item, ExternalServiceMapping externalServiceMapping) {
		if (item.getHubSpotProductId() == null) {
			hubSpotService.createProduct(item);
			externalServiceMapping.setExternalMappingId(item.getHubSpotProductId());
			externalServiceMapping.setLatestMappingSucceeded(true);
		}
	}

	@Override
	public void updateSyncable(
		Long externalServiceMappingId, ExternalServiceMappingSyncableRequestDto externalServiceMappingSyncableRequestDto
	) {
		ExternalServiceMapping externalServiceMapping = externalServiceMappingRepository.getExternalServiceMapping(
				externalServiceMappingId, null, null)
			.orElseThrow(() -> new CustomException(NOT_FOUND_EXTERNAL_SERVICE_MAPPING));

		if (externalServiceMapping.isSyncableSame(externalServiceMappingSyncableRequestDto.getIsSyncable())) {
			throw new CustomException(INVALID_SYNCABLE);
		}

		externalServiceMapping.setIsSyncable(externalServiceMappingSyncableRequestDto.getIsSyncable());
	}

	private void synchronizeOrganization(Organization organization, ExternalServiceMapping externalServiceMapping) {
		User user = userRepository.getUser(organization.getId(), null)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

		if (user.getHubSpotContactId() == null && !existContactByEmail(user)) {
			hubSpotService.createContact(user);
		}

		if (organization.getHubspotCompanyId() == null) {
			hubSpotService.createCompany(organization);
		} else {
			updateCompany(organization);
		}
		hubSpotService.associateContactToCompany(user.getHubSpotContactId(), organization.getHubspotCompanyId());
		externalServiceMapping.setExternalMappingId(organization.getHubspotCompanyId());
		externalServiceMapping.setLatestMappingSucceeded(true);
	}

	private void updateCompany(Organization organization) {
		if (hubSpotService.existCompanyByCompanyId(organization.getHubspotCompanyId())) {
			hubSpotService.updateCompany(organization);
		} else {
			throw new CustomException(ErrorCode.NOT_FOUND_HUBSPOT_COMPANY);
		}
	}

	private void synchronizeUser(User user, ExternalServiceMapping externalServiceMapping) {
		if (!existContactByEmail(user)) {
			hubSpotService.createContact(user);
		} else {
			hubSpotService.updateContact(user);
		}
		externalServiceMapping.setExternalMappingId(user.getHubSpotContactId());
		externalServiceMapping.setLatestMappingSucceeded(true);
	}

	public ExternalServiceMapping getExternalMappingByUserId(Long userId) {
		return externalServiceMappingRepository.getExternalServiceMapping(null, InternalDomain.USER, userId)
			.orElseGet(() -> {
				ExternalServiceMapping externalServiceMapping = new ExternalServiceMapping(
					ExternalDomain.CONTACT, InternalDomain.USER, userId, false);
				externalServiceMappingRepository.save(externalServiceMapping);
				return externalServiceMapping;
			});
	}

}
