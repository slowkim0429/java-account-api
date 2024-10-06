package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseSearchDto;
import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseTrackSdkUsageSearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAndAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseDetailAndItemResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseTrackSdkUsageResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.adapter.outbound.request.OrganizationLicenseAdditionalAttributeSendDto;
import com.virnect.account.adapter.outbound.request.OrganizationLicenseAttributeSendDto;
import com.virnect.account.adapter.outbound.request.OrganizationLicenseSendDto;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.License;
import com.virnect.account.domain.model.LicenseGrade;
import com.virnect.account.domain.model.OrganizationContract;
import com.virnect.account.domain.model.OrganizationLicense;
import com.virnect.account.domain.model.Product;
import com.virnect.account.domain.model.User;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.OrganizationLicenseAttributeService;
import com.virnect.account.port.inbound.OrganizationLicenseService;
import com.virnect.account.port.inbound.SquarsApiService;
import com.virnect.account.port.inbound.UserService;
import com.virnect.account.port.inbound.WorkspaceAPIService;
import com.virnect.account.port.outbound.ItemRepository;
import com.virnect.account.port.outbound.LicenseGradeRepository;
import com.virnect.account.port.outbound.LicenseRepository;
import com.virnect.account.port.outbound.OrganizationContractRepository;
import com.virnect.account.port.outbound.OrganizationLicenseAdditionalAttributeRepository;
import com.virnect.account.port.outbound.OrganizationLicenseAttributeRepository;
import com.virnect.account.port.outbound.OrganizationLicenseRepository;
import com.virnect.account.port.outbound.OrganizationLicenseRevisionRepository;
import com.virnect.account.port.outbound.OrganizationLicenseTrackSdkUsageHistoryRepository;
import com.virnect.account.port.outbound.ProductRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.security.jwt.TokenProvider;
import com.virnect.account.util.ZonedDateTimeUtil;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationLicenseServiceImpl implements OrganizationLicenseService {
	private final OrganizationLicenseRepository organizationLicenseRepository;
	private final OrganizationContractRepository organizationContractRepository;
	private final OrganizationLicenseAttributeRepository organizationLicenseAttributeRepository;
	private final OrganizationLicenseAdditionalAttributeRepository organizationLicenseAdditionalAttributeRepository;
	private final ItemRepository itemRepository;
	private final LicenseRepository licenseRepository;
	private final LicenseGradeRepository licenseGradeRepository;
	private final ProductRepository productRepository;
	private final OrganizationLicenseRevisionRepository organizationLicenseRevisionRepository;
	private final OrganizationLicenseTrackSdkUsageHistoryRepository trackSdkUsageHistoryRepository;

	private final OrganizationLicenseAttributeService organizationLicenseAttributeService;
	private final WorkspaceAPIService workspaceAPIService;
	private final SquarsApiService squarsApiService;
	private final UserService userService;
	private final TokenProvider tokenProvider;

	@Override
	public void sync(
		OrganizationContract organizationContract, Long contractRetainPeriodOfMonthWithAnnuallySubscription
	) {
		Item itemOfLicenseType = itemRepository.getItemById(organizationContract.getItemId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

		organizationLicenseRepository.getOrganizationLicense(
				organizationContract.getOrganizationId(), organizationContract.getContractId())
			.ifPresentOrElse(organizationLicense -> {
				organizationLicense.setExpiredAt(organizationContract.getEndDate());

				if (isTerminationContractAndExpiredContract(
					organizationContract, contractRetainPeriodOfMonthWithAnnuallySubscription)) {
					organizationLicense.setStatusCanceled();
					organizationLicenseAttributeService.unuseOrganizationLicenseAttribute(organizationLicense.getId());
				}

				sendOrganizationLicenseToWorkspaceAndSquars(
					organizationLicense, organizationContract.getRecurringInterval());
			}, () -> {
				OrganizationLicense newOrganizationLicense = createOrganizationLicense(
					organizationContract, itemOfLicenseType);
				organizationLicenseAttributeService.createOrganizationLicenseAttribute(
					newOrganizationLicense.getId(), newOrganizationLicense.getLicenseId(),
					organizationContract.getCouponId()
				);
				sendOrganizationLicenseToWorkspaceAndSquars(
					newOrganizationLicense, organizationContract.getRecurringInterval());
			});
	}

	private boolean isTerminationContractAndExpiredContract(
		OrganizationContract organizationContract, Long contractRetainPeriodOfMonthWithAnnuallySubscription
	) {
		if (!organizationContract.getStatus().isTermination()) {
			return false;
		}
		return contractRetainPeriodOfMonthWithAnnuallySubscription != null
			&& contractRetainPeriodOfMonthWithAnnuallySubscription == 0L;
	}

	private OrganizationLicense createOrganizationLicense(OrganizationContract organizationContract, Item item) {
		License license = licenseRepository.getLicense(item.getLicenseId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE));

		LicenseGrade licenseGrade = licenseGradeRepository.getLicenseGrade(license.getLicenseGradeId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_GRADE));

		Product product = productRepository.getProduct(license.getProductId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

		OrganizationLicense newOrganizationLicense = OrganizationLicense.of(
			organizationContract, product, licenseGrade, license);
		organizationLicenseRepository.save(newOrganizationLicense);
		return newOrganizationLicense;
	}

	@Override
	public void sync(Long organizationId, Long organizationLicenseId) {
		OrganizationLicense organizationLicense = organizationLicenseRepository.getOrganizationLicense(
			organizationLicenseId).orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION_LICENSE));

		if (!organizationLicense.getProductType().isTargetOfSynchronization()) {
			throw new CustomException(INVALID_TARGET_OF_SYNCHRONIZATION);
		}

		if (!organizationLicense.getOrganizationId().equals(organizationId)) {
			throw new CustomException(INVALID_ORGANIZATION);
		}

		RecurringIntervalType recurringIntervalType = null;

		if (organizationLicense.getLicenseGradeType().isFree()) {
			recurringIntervalType = RecurringIntervalType.NONE;
		} else {
			OrganizationContract organizationContract = organizationContractRepository.getOrganizationContract(
					organizationLicense.getContractId())
				.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION_CONTRACT));
			recurringIntervalType = organizationContract.getRecurringInterval();
		}

		OrganizationLicenseSendDto organizationLicenseSendDto = getOrganizationLicenseSendDto(
			organizationLicense, recurringIntervalType);

		workspaceAPIService.syncOrganizationLicenseByAdmin(organizationLicenseSendDto);
		squarsApiService.syncOrganizationLicenseByAdmin(organizationLicenseSendDto);
	}

	@Override
	public void provideFreePlusOrganizationLicense(
		Long organizationId, ZonedDateTime startAt, ProductType productType
	) {
		organizationLicenseRepository.getOrganizationLicense(
				organizationId, OrganizationLicenseStatus.PROCESSING, true, productType, LicenseGradeType.FREE_PLUS)
			.ifPresentOrElse(freePlusOrganizationLicense -> {
				freePlusOrganizationLicense.setStartedAt(startAt);
				if (productType.isTargetOfSynchronization()) {
					sendOrganizationLicenseToWorkspaceAndSquars(freePlusOrganizationLicense, null);
				}
			}, () -> {
				OrganizationLicense newOrganizationLicense = createFreePlusOrganizationLicense(
					organizationId, startAt, productType);
				if (productType.isTargetOfSynchronization()) {
					sendOrganizationLicenseToWorkspaceAndSquars(newOrganizationLicense, null);
				}
			});
	}

	@Override
	public OrganizationLicense createFreePlusOrganizationLicense(
		Long organizationId, ZonedDateTime startAt, ProductType productType
	) {
		Item exposedItemOfLicenseType = itemRepository.getItem(
				LicenseGradeType.FREE_PLUS, ItemType.LICENSE, true, productType)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

		ZonedDateTime expiredAt = ZonedDateTimeUtil.getInfinitePeriod();

		License license = licenseRepository.getLicense(exposedItemOfLicenseType.getLicenseId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE));

		LicenseGrade licenseGrade = licenseGradeRepository.getLicenseGrade(license.getLicenseGradeId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_GRADE));

		Product product = productRepository.getProduct(license.getProductId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

		OrganizationLicense newOrganizationLicense = OrganizationLicense.of(
			organizationId, exposedItemOfLicenseType.getId(), startAt, expiredAt, product, licenseGrade,
			license
		);

		organizationLicenseRepository.save(newOrganizationLicense);

		if (productType.isProductTypeWithAttribute()) {
			organizationLicenseAttributeService.createOrganizationLicenseAttribute(
				newOrganizationLicense.getId(), newOrganizationLicense.getLicenseId());
		}

		return newOrganizationLicense;
	}

	@Override
	public OrganizationLicenseSendDto provideFreePlusOrganizationLicenseAtFirst(
		Long organizationId, ZonedDateTime startAt
	) {
		OrganizationLicense newOrganizationLicense = createFreePlusOrganizationLicense(
			organizationId, startAt, ProductType.SQUARS);
		return getOrganizationLicenseSendDto(newOrganizationLicense, null);

	}

	@Override
	public PageContentResponseDto<OrganizationLicenseResponseDto> getOrganizationLicenses(
		OrganizationLicenseSearchDto organizationLicenseSearchDto, Pageable pageable
	) {
		Page<OrganizationLicenseResponseDto> organizationLicenseResponses = organizationLicenseRepository.getOrganizationLicenseResponses(
			organizationLicenseSearchDto, pageable);
		return new PageContentResponseDto<>(organizationLicenseResponses, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public OrganizationLicenseResponseDto getOrganizationLicense(
		Long organizationLicenseId
	) {
		return organizationLicenseRepository.getOrganizationLicenseResponse(
			organizationLicenseId).orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION_LICENSE));
	}

	@Override
	public void cancelFreePlusOrganizationLicense(Long organizationId, ZonedDateTime expiredAt) {
		organizationLicenseRepository.getOrganizationLicense(
				organizationId, OrganizationLicenseStatus.PROCESSING, ProductType.SQUARS, LicenseGradeType.FREE_PLUS)
			.ifPresent(freePlusOrganizationLicense -> {
				freePlusOrganizationLicense.setStatusCanceled();
				freePlusOrganizationLicense.setExpiredAt(expiredAt);

				organizationLicenseAttributeService.unuseOrganizationLicenseAttribute(
					freePlusOrganizationLicense.getId());

				sendOrganizationLicenseToWorkspaceAndSquars(freePlusOrganizationLicense, null);
			});
	}

	@Override
	@Transactional(readOnly = true)
	public OrganizationLicense getOrganizationLicenseInProgressByProductType(
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

	private List<OrganizationLicense> getAllOrganizationLicenseInProgress(Long organizationId) {
		List<OrganizationLicense> processingOrganizationLicense = new ArrayList<>();
		List<OrganizationLicense> processingOrganizationLicenseCandidates = organizationLicenseRepository.getOrganizationLicenses(
			organizationId, OrganizationLicenseStatus.PROCESSING, null);

		for (OrganizationLicense processingOrganizationLicenseCandidate : processingOrganizationLicenseCandidates) {
			if (isStarted(processingOrganizationLicenseCandidate.getStartedAt()) && !isExpired(
				processingOrganizationLicenseCandidate.getExpiredAt())) {
				processingOrganizationLicense.add(processingOrganizationLicenseCandidate);
			}
		}

		return processingOrganizationLicense;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganizationLicenseDetailAndItemResponseDto> getUsingOrganizationLicenseAndItem(Long organizationId) {
		List<OrganizationLicenseDetailAndItemResponseDto> organizationLicenseDetailAndItemResponseDtos = new ArrayList<>();
		List<OrganizationLicense> organizationLicenses = getAllOrganizationLicenseInProgress(organizationId);

		for (OrganizationLicense organizationLicense : organizationLicenses) {
			Item item = itemRepository.getItemById(organizationLicense.getItemId())
				.orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

			organizationLicenseDetailAndItemResponseDtos.add(
				new OrganizationLicenseDetailAndItemResponseDto(organizationLicense, item.getName()));
		}

		return organizationLicenseDetailAndItemResponseDtos;
	}

	private boolean isExpired(ZonedDateTime expiredAt) {
		return ZonedDateTimeUtil.zoneOffsetOfUTC().isAfter(expiredAt);
	}

	private boolean isStarted(ZonedDateTime startedAt) {
		return ZonedDateTimeUtil.zoneOffsetOfUTC().isEqual(startedAt) ||
			ZonedDateTimeUtil.zoneOffsetOfUTC().isAfter(startedAt);
	}

	@Override
	@Transactional(readOnly = true)
	public OrganizationLicenseAndAttributeResponseDto getMyOrganizationLicense() {
		Long organizationId = SecurityUtil.getCurrentUserOrganizationId();

		OrganizationLicense usingOrganizationLicense = getOrganizationLicenseInProgressByProductType(
			organizationId, ProductType.SQUARS
		);

		List<OrganizationLicenseAttributeResponseDto> organizationLicenseAttributes = organizationLicenseAttributeRepository.getOrganizationLicenseAttributeResponses(
			usingOrganizationLicense.getId());

		return new OrganizationLicenseAndAttributeResponseDto(
			usingOrganizationLicense, organizationLicenseAttributes);
	}

	@Override
	@Transactional(readOnly = true)
	public void sendOrganizationLicenseToWorkspaceAndSquars(
		OrganizationLicense organizationLicense, RecurringIntervalType contractRecurringInterval
	) {
		User user = userService.getUserByOrganizationId(organizationLicense.getOrganizationId());
		TokenResponseDto tokenResponseDto = tokenProvider.createToken(user.getEmail());
		String authorizationHeaderValue = tokenProvider.createAuthorizationHeaderValue(tokenResponseDto);

		OrganizationLicenseSendDto organizationLicenseSendDto = getOrganizationLicenseSendDto(
			organizationLicense, contractRecurringInterval);
		workspaceAPIService.syncOrganizationLicense(organizationLicenseSendDto, authorizationHeaderValue);
		squarsApiService.syncOrganizationLicense(organizationLicenseSendDto, authorizationHeaderValue);
	}

	@Override
	public List<OrganizationLicenseRevisionResponseDto> getOrganizationLicenseRevisions(Long organizationLicenseId) {
		return organizationLicenseRevisionRepository.getOrganizationLicenseRevisionResponses(organizationLicenseId);
	}

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<OrganizationLicenseTrackSdkUsageResponseDto> getTrackSdkUsageHistories(
		OrganizationLicenseTrackSdkUsageSearchDto organizationLicenseTrackSdkUsageSearchDto, PageRequest pageable
	) {
		Page<OrganizationLicenseTrackSdkUsageResponseDto> responses = trackSdkUsageHistoryRepository.getTrackSdkHistoryResponses(
			organizationLicenseTrackSdkUsageSearchDto, pageable);
		return new PageContentResponseDto<>(responses, pageable);
	}

	private OrganizationLicenseSendDto getOrganizationLicenseSendDto(
		OrganizationLicense organizationLicense, RecurringIntervalType contractRecurringInterval
	) {
		List<OrganizationLicenseAttributeSendDto> organizationLicenseAttributes = organizationLicenseAttributeRepository.getOrganizationLicenseAttributes(
			organizationLicense.getId()).stream().map(
			OrganizationLicenseAttributeSendDto::from).collect(Collectors.toList());

		List<OrganizationLicenseAdditionalAttributeSendDto> organizationLicenseAdditionalAttributes = organizationLicenseAdditionalAttributeRepository.getOrganizationLicenseAdditionalAttributes(
				organizationLicense.getId())
			.stream().map(OrganizationLicenseAdditionalAttributeSendDto::from).collect(Collectors.toList());

		return OrganizationLicenseSendDto.builder()
			.organizationLicense(organizationLicense)
			.recurringIntervalType(contractRecurringInterval)
			.attributes(organizationLicenseAttributes)
			.additionalAttributes(organizationLicenseAdditionalAttributes)
			.build();
	}
}
