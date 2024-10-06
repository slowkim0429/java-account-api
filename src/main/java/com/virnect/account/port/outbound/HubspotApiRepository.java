package com.virnect.account.port.outbound;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.account.adapter.outbound.request.HubSpotCompanyRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotCompanyUpdateRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotContactRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotContactUpdateRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotProductRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotTokenRefreshRequestDto;
import com.virnect.account.adapter.outbound.response.HubSpotCompanyResponseDto;
import com.virnect.account.adapter.outbound.response.HubSpotContactResponseDto;
import com.virnect.account.adapter.outbound.response.HubSpotProductResponseDto;
import com.virnect.account.adapter.outbound.response.HubSpotTokenResponse;

@FeignClient(name = "hubspot", url = "${hubspot.url}", primary = false)
public interface HubspotApiRepository {

	@PostMapping(value = "/oauth/v1/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	HubSpotTokenResponse getHubspotToken(
		@ModelAttribute HubSpotTokenRefreshRequestDto hubspotTokenRefreshRequestDto
	);

	@GetMapping(value = "/crm/v3/objects/contacts/{email}")
	HubSpotContactResponseDto getContactByEmail(
		@PathVariable("email") String email,
		@RequestParam("idProperty") String idProperty,
		@RequestHeader("Authorization") String authorizationToken
	);

	@PostMapping(value = "/crm/v3/objects/contacts")
	HubSpotContactResponseDto createContact(
		@RequestBody HubSpotContactRequestDto hubSpotContactRequestDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@PostMapping(value = "/crm/v3/objects/companies")
	HubSpotCompanyResponseDto createCompany(
		@RequestBody HubSpotCompanyRequestDto hubSpotCompanyRequestDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@PutMapping(value = "/crm/v3/objects/contacts/{contactId}/associations/COMPANY/{companyId}/contact_to_company")
	void associationContactToCompany(
		@PathVariable("contactId") Long contactId,
		@PathVariable("companyId") Long companyId,
		@RequestHeader("Authorization") String authorizationToken
	);

	@PatchMapping("/crm/v3/objects/contacts/{contactId}")
	void updateContact(
		@PathVariable("contactId") Long contactId,
		@RequestBody HubSpotContactUpdateRequestDto hubSpotContactUpdateRequestDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@GetMapping("/crm/v3/objects/companies/{companyId}")
	void getCompanyByCompanyId(
		@PathVariable("companyId") Long companyId,
		@RequestHeader("Authorization") String authorizationToken
	);

	@PatchMapping("/crm/v3/objects/companies/{companyId}")
	void updateCompany(
		@PathVariable("companyId") Long companyId,
		@RequestBody HubSpotCompanyUpdateRequestDto hubSpotCompanyUpdateRequestDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@PostMapping(value = "/crm/v3/objects/products")
	HubSpotProductResponseDto createProduct(
		@RequestBody HubSpotProductRequestDto hubSpotProductRequestDto,
		@RequestHeader("Authorization") String authorizationToken
	);
}
