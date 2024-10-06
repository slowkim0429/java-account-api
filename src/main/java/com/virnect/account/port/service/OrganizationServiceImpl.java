package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationSearchDto;
import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserResponseDto;
import com.virnect.account.adapter.outbound.request.OrganizationSendDto;
import com.virnect.account.domain.enumclass.DateRangeType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.domain.model.ServiceLocaleState;
import com.virnect.account.domain.model.ServiceRegionLocaleMapping;
import com.virnect.account.domain.model.User;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.ContractAPIService;
import com.virnect.account.port.inbound.ExternalServiceMappingService;
import com.virnect.account.port.inbound.OrganizationService;
import com.virnect.account.port.inbound.UserService;
import com.virnect.account.port.outbound.LocaleRepository;
import com.virnect.account.port.outbound.OrganizationRepository;
import com.virnect.account.port.outbound.OrganizationRevisionRepository;
import com.virnect.account.port.outbound.StateRepository;
import com.virnect.account.port.outbound.UserRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.security.service.CustomUserDetails;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrganizationServiceImpl implements OrganizationService {
	private static final String LOCALE_CODE_OF_STATES = "US|CA";
	private final UserService userService;
	private final ExternalServiceMappingService externalServiceMappingService;
	private final UserRepository userRepository;
	private final OrganizationRepository organizationRepository;
	private final OrganizationRevisionRepository organizationRevisionRepository;
	private final StateRepository stateRepository;
	private final LocaleRepository localeRepository;
	private final ContractAPIService contractAPIService;

	@Override
	public Organization create(User user, ServiceRegionLocaleMapping locale) {
		if (user.getOrganizationStatus().isUse()) {
			throw new CustomException(INVALID_OTHER_ORGANIZATION_USER);
		}

		String initialOrganizationName = createInitialOrganizationName(user.getEmail());

		Organization newOrganization = Organization.create(
			initialOrganizationName, user.getEmail(), user.getLocaleId(), user.getLocaleCode(), locale.getName());
		organizationRepository.save(newOrganization);

		user.setOrganizationId(newOrganization.getId());
		user.setOrganizationUse(UseStatus.USE);

		userService.setUserRole(user.getId(), Role.ROLE_ORGANIZATION_OWNER, UseStatus.USE);
		return newOrganization;
	}

	@Override
	public void update(Long organizationId, OrganizationUpdateRequestDto organizationUpdateRequestDto) {
		if (!organizationId.equals(SecurityUtil.getCurrentUserOrganizationId())) {
			throw new CustomException(INVALID_AUTHORIZATION);
		}

		Organization requestOrganization = organizationRepository.getOrganization(organizationId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION));

		ServiceRegionLocaleMapping requestOrganizationLocale = localeRepository.getLocaleById(
			organizationUpdateRequestDto.getLocaleId()).orElseThrow(() -> new CustomException(NOT_FOUND_LOCALE));

		if (requestOrganizationLocale.getCode().matches(LOCALE_CODE_OF_STATES)) {
			if (StringUtils.isBlank(organizationUpdateRequestDto.getStateCode()) || StringUtils.isNotBlank(
				organizationUpdateRequestDto.getProvince())) {
				throw new CustomException(INVALID_INPUT_VALUE);
			}

			ServiceLocaleState serviceLocaleState = stateRepository.getState(
				organizationUpdateRequestDto.getStateCode()).orElseThrow(() -> new CustomException(NOT_FOUND_STATE));

			if (!serviceLocaleState.getLocaleCode().equals(requestOrganizationLocale.getCode())) {
				throw new CustomException(INVALID_LOCALE);
			}

			requestOrganization.updateOrganizationState(serviceLocaleState.getCode(), serviceLocaleState.getName());
		} else {
			if (StringUtils.isNotBlank(organizationUpdateRequestDto.getStateCode())) {
				throw new CustomException(INVALID_INPUT_VALUE);
			}

			requestOrganization.updateOrganizationState(null, null);
		}

		requestOrganization.update(organizationUpdateRequestDto, requestOrganizationLocale);

		OrganizationSendDto organizationSendDto = OrganizationSendDto.from(
			requestOrganization);

		contractAPIService.syncOrganization(requestOrganization.getId(), organizationSendDto);

		externalServiceMappingService.updateOrganization(requestOrganization);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponseDto getOrganizationUser(Long organizationId) {
		return userRepository.getUserByOrganizationId(organizationId).map(UserResponseDto::new)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}

	@Override
	public OrganizationResponseDto getCurrentOrganization(CustomUserDetails customUserDetails) {
		Long userOrganizationId = customUserDetails.getOrganizationId();
		if (userOrganizationId == null || UseStatus.UNUSE.equals(customUserDetails.getOrganizationStatus())) {
			throw new CustomException(INVALID_ORGANIZATION);
		}

		Organization organization = organizationRepository.getOrganization(userOrganizationId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION));

		organization.encodeOrganizationData();

		return OrganizationResponseDto.from(organization);
	}

	@Override
	public OrganizationResponseDto getOrganizationById(Long organizationId) {
		Organization organization = organizationRepository.getOrganization(organizationId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION));

		organization.encodeOrganizationData();
		return OrganizationResponseDto.from(organization);
	}

	public PageContentResponseDto<OrganizationResponseDto> getOrganizations(
		OrganizationSearchDto organizationSearchDto, Pageable pageable
	) {
		final DateRangeType requestDateRangeType = organizationSearchDto.dateRangeTypeValueOf();
		ZonedDateTime startDate = requestDateRangeType.getStartDate();
		ZonedDateTime endDate = requestDateRangeType.getEndDate();

		if (requestDateRangeType.isCustom()) {
			startDate = organizationSearchDto.getStartDate();
			endDate = organizationSearchDto.getEndDate();
		}

		Page<OrganizationResponseDto> organizationPageResult = organizationRepository.getOrganizations(
			organizationSearchDto, startDate, endDate, pageable).map(organization -> {
				organization.encodeOrganizationData();
				return OrganizationResponseDto.from(organization);
			}
		);

		return new PageContentResponseDto<>(organizationPageResult, pageable);
	}

	@Override
	public void synchronizeOrganizationByAdmin(Long organizationId) {
		Organization organization = organizationRepository.getOrganization(organizationId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION));

		organization.encodeOrganizationData();
		OrganizationSendDto organizationSendDto = OrganizationSendDto.from(organization);

		contractAPIService.syncOrganizationByAdmin(organizationId, organizationSendDto);
	}

	private String createInitialOrganizationName(String email) {
		String organizationName = email.split("@")[0];
		return organizationName.length() > 20 ? organizationName.substring(0, 20) : organizationName;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganizationRevisionResponseDto> getOrganizationRevision(Long organizationId) {
		return organizationRevisionRepository.getOrganizationRevisionResponses(organizationId);
	}
}
