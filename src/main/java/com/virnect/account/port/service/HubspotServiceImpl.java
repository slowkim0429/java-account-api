package com.virnect.account.port.service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.HubSpotAccessTokenResponseDto;
import com.virnect.account.adapter.outbound.request.CompanyRequestDto;
import com.virnect.account.adapter.outbound.request.CompanyUpdateRequestDto;
import com.virnect.account.adapter.outbound.request.ContactRequestDto;
import com.virnect.account.adapter.outbound.request.ContactUpdateRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotCompanyRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotCompanyUpdateRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotContactRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotContactUpdateRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotProductRequestDto;
import com.virnect.account.adapter.outbound.request.HubSpotTokenRefreshRequestDto;
import com.virnect.account.adapter.outbound.request.ProductRequestDto;
import com.virnect.account.adapter.outbound.response.HubSpotCompanyResponseDto;
import com.virnect.account.adapter.outbound.response.HubSpotContactResponseDto;
import com.virnect.account.adapter.outbound.response.HubSpotProductResponseDto;
import com.virnect.account.adapter.outbound.response.HubSpotTokenResponse;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.domain.model.User;
import com.virnect.account.port.inbound.HubspotService;
import com.virnect.account.port.outbound.HubspotApiRepository;
import com.virnect.account.port.outbound.RedisRepository;

@Service
@Profile("production")
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class HubspotServiceImpl implements HubspotService {
	private static final String HUBSPOT_TOKEN_GRANT_TYPE = "refresh_token";
	private static final String HUBSPOT_TOKEN_TYPE = "Bearer";
	private static final String HUBSPOT_ACCESS_TOKEN_KEY = "HubspotAccessToken";
	private final HubspotApiRepository hubspotApiRepository;
	private final RedisRepository redisRepository;
	@Value("${hubspot.client-id}")
	private String clientId;
	@Value("${hubspot.client-secret}")
	private String clientSecret;
	@Value("${hubspot.redirect-uri}")
	private String redirectUri;
	@Value("${hubspot.refresh-token}")
	private String refreshToken;

	@Override
	public HubSpotAccessTokenResponseDto getHubSpotToken() {
		HubSpotTokenRefreshRequestDto hubSpotTokenRefreshRequestDto = HubSpotTokenRefreshRequestDto.builder()
			.grantType(HUBSPOT_TOKEN_GRANT_TYPE)
			.clientId(clientId)
			.clientSecret(clientSecret)
			.redirectUri(redirectUri)
			.refreshToken(refreshToken)
			.build();

		HubSpotTokenResponse hubSpotTokenResponse = hubspotApiRepository.getHubspotToken(hubSpotTokenRefreshRequestDto);

		redisRepository.setObjectValue(
			HUBSPOT_ACCESS_TOKEN_KEY, hubSpotTokenResponse.getAccessToken(),
			getHubspotTokenExpire(hubSpotTokenResponse.getExpiresIn())
		);

		return HubSpotAccessTokenResponseDto.from(hubSpotTokenResponse);
	}

	private long getHubspotTokenExpire(Long expiresIn) {
		return Duration.ofSeconds(expiresIn).toMillis() - Duration.ofMinutes(5L).toMillis();
	}

	@Override
	public void createProduct(Item item) {
		String authorizationToken = this.getHubSpotAuthorizationToken();

		ProductRequestDto productRequestDto = ProductRequestDto.builder()
			.hsCostOfGoodsSold(item.getAmount())
			.itemId(item.getId())
			.name(item.getName())
			.price(item.getAmount())
			.itemType(getHubSpotItemType(item.getRecurringInterval()))
			.recurringInterval(getHubSpotRecurringInterval(item.getRecurringInterval()))
			.build();

		HubSpotProductRequestDto hubSpotProductRequestDto = new HubSpotProductRequestDto(productRequestDto);

		HubSpotProductResponseDto response = hubspotApiRepository.createProduct(
			hubSpotProductRequestDto, authorizationToken);

		item.setHubSpotProductId(response.getHubSpotProductId());
	}

	public boolean existContactByEmail(User user) {
		try {
			HubSpotContactResponseDto hubSpotContactResponseDto = this.getContactByEmail(user);
			user.setHubSpotContactId(hubSpotContactResponseDto.getHubspotContactId());
			return true;
		} catch (FeignException e) {
			if (e.status() == HttpStatus.NOT_FOUND.value()) {
				return false;
			} else {
				throw e;
			}
		}
	}

	@Override
	public boolean existCompanyByCompanyId(Long companyId) {
		try {
			this.getCompanyByCompanyId(companyId);
			return true;
		} catch (FeignException e) {
			if (e.status() == HttpStatus.NOT_FOUND.value()) {
				return false;
			} else {
				throw e;
			}
		}
	}

	@Override
	public void updateCompany(Organization organization) {
		String authorizationToken = this.getHubSpotAuthorizationToken();

		if (organization.getHubspotCompanyId() == null) {
			this.createCompany(organization);
			return;
		}

		CompanyUpdateRequestDto companyUpdateRequestDto = CompanyUpdateRequestDto.from(organization);

		HubSpotCompanyUpdateRequestDto hubSpotCompanyUpdateRequestDto = new HubSpotCompanyUpdateRequestDto(
			companyUpdateRequestDto);
		hubspotApiRepository.updateCompany(
			organization.getHubspotCompanyId(), hubSpotCompanyUpdateRequestDto, authorizationToken);
	}

	@Override
	public void updateLoginDate(Long hubspotContactId, LocalDate loginDate) {
		if (hubspotContactId == null || hubspotContactId == 0L) {
			return;
		}
		long loginUTCMidnightTimestamp = Timestamp.from(loginDate.atStartOfDay(ZoneOffset.UTC).toInstant())
			.getTime();

		ContactUpdateRequestDto contactUpdateRequestDto = ContactUpdateRequestDto.from(loginUTCMidnightTimestamp);

		this.updateContact(hubspotContactId, contactUpdateRequestDto);
	}

	@Override
	public void updateContact(User user) {
		ContactUpdateRequestDto contactUpdateRequestDto = ContactUpdateRequestDto.of(
			user.getNickname(), user.getStatus(), user.getMarketInfoReceive(), user.getLocaleCode());

		this.updateContact(user.getHubSpotContactId(), contactUpdateRequestDto);
	}

	private String getHubSpotAuthorizationToken() {
		String hubSpotAccessToken = redisRepository.getStringValue(HUBSPOT_ACCESS_TOKEN_KEY);

		if (StringUtils.isNotBlank(hubSpotAccessToken)) {
			return HUBSPOT_TOKEN_TYPE + StringUtils.SPACE + hubSpotAccessToken;
		}
		return HUBSPOT_TOKEN_TYPE + StringUtils.SPACE + this.getHubSpotToken().getAccessToken();
	}

	@Override
	public void createCompany(
		Organization organization
	) {
		String authorizationToken = this.getHubSpotAuthorizationToken();
		CompanyRequestDto companyRequestDto = CompanyRequestDto.builder()
			.email(organization.getEmail())
			.name(organization.getName())
			.localeCode(organization.getLocaleCode())
			.build();

		HubSpotCompanyRequestDto hubSpotCompanyRequestDto = new HubSpotCompanyRequestDto(companyRequestDto);

		HubSpotCompanyResponseDto response = hubspotApiRepository.createCompany(
			hubSpotCompanyRequestDto, authorizationToken);

		organization.setHubspotCompanyId(response.getHubspotCompanyId());
	}

	@Override
	public void createContact(
		User user
	) {
		String authorizationToken = this.getHubSpotAuthorizationToken();
		ContactRequestDto contactRequestDto = ContactRequestDto.builder()
			.email(user.getEmail())
			.nickname(user.getNickname())
			.localeCode(user.getLocaleCode())
			.marketInfoReceive(user.getMarketInfoReceive())
			.build();

		HubSpotContactRequestDto hubSpotContactRequestDto = new HubSpotContactRequestDto(contactRequestDto);

		HubSpotContactResponseDto response = hubspotApiRepository.createContact(
			hubSpotContactRequestDto, authorizationToken);

		user.setHubSpotContactId(response.getHubspotContactId());
	}

	private void updateContact(Long hubSpotContactId, ContactUpdateRequestDto contactUpdateRequestDto) {
		String authorizationToken = this.getHubSpotAuthorizationToken();

		HubSpotContactUpdateRequestDto hubSpotContactUpdateRequestDto = new HubSpotContactUpdateRequestDto(
			contactUpdateRequestDto);

		hubspotApiRepository.updateContact(
			hubSpotContactId, hubSpotContactUpdateRequestDto, authorizationToken);
	}

	private HubSpotContactResponseDto getContactByEmail(
		User user
	) {
		String authorizationToken = this.getHubSpotAuthorizationToken();
		return hubspotApiRepository.getContactByEmail(
			user.getEmail(), "email", authorizationToken);
	}

	private void getCompanyByCompanyId(Long companyId) {
		String authorizationToken = this.getHubSpotAuthorizationToken();
		hubspotApiRepository.getCompanyByCompanyId(companyId, authorizationToken);
	}

	@Override
	public void associateContactToCompany(Long contactId, Long companyId) {
		String authorizationToken = this.getHubSpotAuthorizationToken();
		hubspotApiRepository.associationContactToCompany(contactId, companyId, authorizationToken);
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
