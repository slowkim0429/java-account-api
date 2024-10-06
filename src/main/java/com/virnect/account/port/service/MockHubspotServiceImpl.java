package com.virnect.account.port.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.account.adapter.inbound.dto.response.HubSpotAccessTokenResponseDto;
import com.virnect.account.adapter.outbound.request.CompanyUpdateRequestDto;
import com.virnect.account.adapter.outbound.request.ContactUpdateRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotCompanyUpdateRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotProductRequestDto;
import com.virnect.account.adapter.outbound.request.ProductRequestDto;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.domain.model.User;
import com.virnect.account.port.inbound.HubspotService;

@Service
@Profile("!production")
@Slf4j
@RequiredArgsConstructor
public class MockHubspotServiceImpl implements HubspotService {
	@Override
	public HubSpotAccessTokenResponseDto getHubSpotToken() {
		log.info("MockHubspotServiceImpl.getHubSpotToken");
		return HubSpotAccessTokenResponseDto.mock();
	}

	@Override
	public void updateCompany(
		Organization organization
	) {
		CompanyUpdateRequestDto companyUpdateRequestDto = CompanyUpdateRequestDto.from(organization);

		HubSpotCompanyUpdateRequestDto hubSpotCompanyUpdateRequestDto = new HubSpotCompanyUpdateRequestDto(
			companyUpdateRequestDto);
		log.info("MockHubspotServiceImpl.updateCompany");
		log.info(
			"companyId = {}, hubSpotCompanyUpdateRequestDto = {}", organization.getHubspotCompanyId()
			, hubSpotCompanyUpdateRequestDto
		);
	}

	@Override
	public void updateLoginDate(Long hubspotContactId, LocalDate loginDate) {
		long loginUTCMidnightTimestamp = Timestamp.from(loginDate.atStartOfDay(ZoneOffset.UTC).toInstant()).getTime();

		ContactUpdateRequestDto contactUpdateRequestDto = ContactUpdateRequestDto.from(loginUTCMidnightTimestamp);

		log.info("contactUpdateRequestDto = {}", contactUpdateRequestDto);
	}

	@Override
	public void createProduct(Item item) {
		ProductRequestDto productRequestDto = ProductRequestDto.builder()
			.hsCostOfGoodsSold(item.getAmount())
			.itemId(item.getId())
			.name(item.getName())
			.price(item.getAmount())
			.itemType(getHubSpotItemType(item.getRecurringInterval()))
			.recurringInterval(getHubSpotRecurringInterval(item.getRecurringInterval()))
			.build();

		HubSpotProductRequestDto hubSpotProductRequestDto = new HubSpotProductRequestDto(productRequestDto);
		log.info("MockHubspotServiceImpl.createProduct");
		log.info("hubSpotProductRequestDto = {}", hubSpotProductRequestDto);
	}

	@Override
	public void updateContact(User user) {
		ContactUpdateRequestDto contactUpdateRequestDto = ContactUpdateRequestDto.of(
			user.getNickname(), user.getStatus(), user.getMarketInfoReceive(), user.getLocaleCode());
		log.info("MockHubspotServiceImpl.sendUpdateUser");
		log.info("contactUpdateRequestDto = {}", contactUpdateRequestDto);
	}

	@Override
	public boolean existContactByEmail(User user) {
		log.info("MockHubspotServiceImpl.existContactByEmail");
		log.info("user.getHubSpotContactId() = " + user.getHubSpotContactId());
		return false;
	}

	@Override
	public void createCompany(Organization organization) {
		log.info("MockHubspotServiceImpl.createCompany");
		log.info("organization.getHubspotCompanyId() = " + organization.getHubspotCompanyId());
	}

	@Override
	public void createContact(User user) {
		log.info("MockHubspotServiceImpl.createContact");
		log.info("user.getHubSpotContactId() = " + user.getHubSpotContactId());

	}

	@Override
	public void associateContactToCompany(Long contactId, Long companyId) {
		log.info("MockHubspotServiceImpl.associateContactToCompany");
		log.info("contactId = " + contactId + ", companyId = " + companyId);
	}

	@Override
	public boolean existCompanyByCompanyId(Long companyId) {
		log.info("MockHubspotServiceImpl.existCompanyByCompanyId");
		log.info("companyId = " + companyId);
		return false;
	}

	private String getHubSpotItemType(RecurringIntervalType recurringIntervalType) {
		return RecurringIntervalType.NONE.equals(recurringIntervalType) ? "attribute" : "subscription";
	}

	private String getHubSpotRecurringInterval(RecurringIntervalType recurringIntervalType) {
		if (recurringIntervalType.equals(RecurringIntervalType.MONTH)) {
			return "monthly";
		}
		if (recurringIntervalType.equals(RecurringIntervalType.YEAR)) {
			return "annually";
		}
		return null;
	}
}
