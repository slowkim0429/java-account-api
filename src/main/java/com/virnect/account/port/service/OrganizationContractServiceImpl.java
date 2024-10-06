package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationContractRequestDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationContractResponseDto;
import com.virnect.account.domain.enumclass.ContractStatus;
import com.virnect.account.domain.enumclass.OrganizationStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.domain.model.OrganizationContract;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.OrganizationContractService;
import com.virnect.account.port.inbound.OrganizationLicenseAdditionalAttributeService;
import com.virnect.account.port.inbound.OrganizationLicenseService;
import com.virnect.account.port.outbound.OrganizationContractRepository;
import com.virnect.account.port.outbound.OrganizationRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.util.ZonedDateTimeUtil;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationContractServiceImpl implements OrganizationContractService {
	private final OrganizationLicenseService organizationLicenseService;
	private final OrganizationLicenseAdditionalAttributeService organizationLicenseAdditionalAttributeService;
	private final OrganizationContractRepository organizationContractRepository;
	private final OrganizationRepository organizationRepository;

	@Override
	public void sync(OrganizationContractRequestDto organizationContractRequestDto) {
		if (!organizationContractRequestDto.getStatus().isOrganizationLicenseHandleStatus()) {
			return;
		}

		Organization organization = organizationRepository.getOrganization(
				organizationContractRequestDto.getOrganizationId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION));

		if (!OrganizationStatus.APPROVED.equals(organization.getStatus())) {
			throw new CustomException(NOT_FOUND_ORGANIZATION);
		}

		organizationContractRepository.getOrganizationContract(organizationContractRequestDto.getContractId())
			.ifPresentOrElse(organizationContract -> {

				organizationContract.setStatus(organizationContractRequestDto.getStatus());
				organizationContract.setStartDate(organizationContractRequestDto.getStartDate());
				organizationContract.setEndDate(organizationContractRequestDto.getEndDate());

				syncOrganizationLicense(organizationContract, organizationContractRequestDto.getUsedMonth());
			}, () -> {
				OrganizationContract organizationContract = OrganizationContract.from(organizationContractRequestDto);
				organizationContractRepository.save(organizationContract);
				syncOrganizationLicense(organizationContract, organizationContractRequestDto.getUsedMonth());
			});
	}

	private void syncOrganizationLicense(
		OrganizationContract organizationContract, Long contractRetainPeriodOfMonthWithAnnuallySubscription
	) {
		if (organizationContract.getItemType().isLicense()) {
			if (isStatusForFreeLicenseCancel(organizationContract.getStatus())) {
				organizationLicenseService.cancelFreePlusOrganizationLicense(
					organizationContract.getOrganizationId(), ZonedDateTimeUtil.zoneOffsetOfUTC());
			}

			organizationLicenseService.sync(organizationContract, contractRetainPeriodOfMonthWithAnnuallySubscription);

			if (isStatusForFreeLicenseProvide(organizationContract.getStatus())) {
				organizationLicenseService.provideFreePlusOrganizationLicense(
					organizationContract.getOrganizationId(), organizationContract.getEndDate().plusSeconds(1L),
					ProductType.SQUARS
				);
			}
		}

		if (organizationContract.getItemType().isAttribute()) {
			organizationLicenseAdditionalAttributeService.sync(organizationContract);
		}
	}

	private boolean isStatusForFreeLicenseCancel(ContractStatus contractStatus) {
		return contractStatus.isProcessing();
	}

	private boolean isStatusForFreeLicenseProvide(ContractStatus contractStatus) {
		ContractStatus statusOfSubscriptionPaymentFail = ContractStatus.PENDING;

		return contractStatus.isCanceled() ||
			contractStatus.isTermination() ||
			statusOfSubscriptionPaymentFail.equals(contractStatus);
	}

	@Override
	@Transactional(readOnly = true)
	public OrganizationContractResponseDto getOrganizationContractByAdmin(Long contractId) {
		return organizationContractRepository.getOrganizationContractResponse(contractId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION_CONTRACT));
	}

	@Override
	@Transactional(readOnly = true)
	public OrganizationContractResponseDto getOrganizationContract(Long contractId) {
		OrganizationContractResponseDto organizationContractResponseDto = organizationContractRepository.getOrganizationContractResponse(
				contractId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ORGANIZATION_CONTRACT));

		Long currentUserOrganizationId = SecurityUtil.getCurrentUserOrganizationId();
		Long contractOrganizationId = organizationContractResponseDto.getOrganizationId();

		if (!Objects.equals(currentUserOrganizationId, contractOrganizationId)) {
			throw new CustomException(INVALID_AUTHORIZATION);
		}

		return organizationContractResponseDto;
	}
}
