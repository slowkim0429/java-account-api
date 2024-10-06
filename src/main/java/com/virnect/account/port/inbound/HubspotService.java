package com.virnect.account.port.inbound;

import java.time.LocalDate;

import com.virnect.account.adapter.inbound.dto.response.HubSpotAccessTokenResponseDto;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.domain.model.User;

public interface HubspotService {
	HubSpotAccessTokenResponseDto getHubSpotToken();

	void createProduct(Item item);

	void updateCompany(Organization organization);

	void updateLoginDate(Long hubspotContactId, LocalDate loginDate);

	void updateContact(User user);

	boolean existContactByEmail(User user);

	void createCompany(
		Organization organization
	);

	void createContact(
		User user
	);

	void associateContactToCompany(Long contactId, Long companyId);

	boolean existCompanyByCompanyId(Long companyId);
}
