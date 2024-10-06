package com.virnect.account.port.inbound;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.hubspot.ExternalServiceMappingSearchDto;
import com.virnect.account.adapter.inbound.dto.request.hubspot.ExternalServiceMappingSyncableRequestDto;
import com.virnect.account.adapter.inbound.dto.response.ExternalServiceMappingRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.HubspotMappingResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.domain.model.User;

public interface ExternalServiceMappingService {
	void register(User newUser, Organization newOrganization);

	void createItem(Item item);

	void updateOrganization(Organization organization);

	void updateLoginDate(Long hubspotContactId, LocalDate loginDate);

	void updateUser(User user);

	PageContentResponseDto<HubspotMappingResponseDto> getHubspotServiceMappings(
		ExternalServiceMappingSearchDto externalServiceMappingSearchDto, Pageable pageable
	);

	void synchronizeUserAndHubspotMapping(Long userId);

	void synchronizeOrganizationAndHubspotMapping(Long organizationId);

	void synchronizeItemAndHubspotMapping(Long itemId);

	PageContentResponseDto<ExternalServiceMappingRevisionResponseDto> getRevisions(
		Long externalServiceMappingId, Pageable pageable
	);

	void updateSyncable(
		Long externalServiceMappingId, ExternalServiceMappingSyncableRequestDto externalServiceMappingSyncableRequestDto
	);
}
