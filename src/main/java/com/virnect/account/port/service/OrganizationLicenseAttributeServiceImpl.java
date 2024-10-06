package com.virnect.account.port.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAttributeRevisionResponseDto;
import com.virnect.account.domain.enumclass.DependencyType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Coupon;
import com.virnect.account.domain.model.LicenseAttribute;
import com.virnect.account.domain.model.OrganizationLicenseAttribute;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.OrganizationLicenseAttributeService;
import com.virnect.account.port.outbound.CouponRepository;
import com.virnect.account.port.outbound.LicenseAttributeRepository;
import com.virnect.account.port.outbound.OrganizationLicenseAttributeRepository;
import com.virnect.account.port.outbound.OrganizationLicenseAttributeRevisionRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationLicenseAttributeServiceImpl implements OrganizationLicenseAttributeService {
	private final OrganizationLicenseAttributeRepository organizationLicenseAttributeRepository;
	private final LicenseAttributeRepository licenseAttributeRepository;
	private final OrganizationLicenseAttributeRevisionRepository organizationLicenseAttributeRevisionRepository;
	private final CouponRepository couponRepository;

	@Override
	public void createOrganizationLicenseAttribute(Long organizationLicenseId, Long licenseId) {
		this.createOrganizationLicenseAttribute(organizationLicenseId, licenseId, null);
	}

	@Override
	public void createOrganizationLicenseAttribute(Long organizationLicenseId, Long licenseId, Long couponId) {
		List<LicenseAttribute> licenseAttributes = licenseAttributeRepository.getLicenseAttributes(
			licenseId, UseStatus.USE, DependencyType.DEPENDENCE);

		LicenseAttributeType benifitAttributeType = null;
		long benefitAttributeDataValue = 0L;

		if (couponId != null) {
			Coupon coupon = couponRepository.getCoupon(couponId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COUPON));

			if (coupon.getCouponType().isUpgradeLicenseAttribute()) {
				benifitAttributeType = LicenseAttributeType.valueOf(coupon.getBenefitOption().name());
				benefitAttributeDataValue = coupon.getBenefitValue();
			}
		}

		for (LicenseAttribute licenseAttribute : licenseAttributes) {
			String upgradedDataValue = getUpgradedDataValue(
				benifitAttributeType, benefitAttributeDataValue, licenseAttribute);

			OrganizationLicenseAttribute organizationLicenseAttribute = OrganizationLicenseAttribute.of(
				organizationLicenseId, licenseAttribute, upgradedDataValue);

			organizationLicenseAttributeRepository.save(organizationLicenseAttribute);
		}
	}

	private String getUpgradedDataValue(
		LicenseAttributeType benifitLicenseAttributeType, long benefitLicenseAttributeDataValue,
		LicenseAttribute licenseAttribute
	) {
		if (!licenseAttribute.getAttributeType().equals(benifitLicenseAttributeType)) {
			return licenseAttribute.getDataValue();
		}

		return String.valueOf(Long.parseLong(licenseAttribute.getDataValue()) + benefitLicenseAttributeDataValue);
	}

	@Override
	public void unuseOrganizationLicenseAttribute(Long organizationLicenseId) {
		List<OrganizationLicenseAttribute> organizationLicenseAttributes = organizationLicenseAttributeRepository.getOrganizationLicenseAttributes(
			organizationLicenseId);
		for (OrganizationLicenseAttribute organizationLicenseAttribute : organizationLicenseAttributes) {
			organizationLicenseAttribute.setUnused();
		}
	}

	@Override
	public List<OrganizationLicenseAttributeDetailResponseDto> getOrganizationLicenseAttributes(
		Long organizationLicenseId
	) {
		return organizationLicenseAttributeRepository.getOrganizationLicenseAttributeDetailResponses(
			organizationLicenseId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganizationLicenseAttributeRevisionResponseDto> getRevisions(
		Long organizationLicenseId, LicenseAttributeType licenseAttributeType
	) {
		return organizationLicenseAttributeRevisionRepository.getRevisionResponses(
			organizationLicenseId, licenseAttributeType);
	}
}
