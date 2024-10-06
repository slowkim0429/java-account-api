package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseAdditionalAttributeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseAttributeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseSearchDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.DependencyType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.License;
import com.virnect.account.domain.model.LicenseAttribute;
import com.virnect.account.domain.model.LicenseGrade;
import com.virnect.account.domain.model.Product;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.LicenseGradeService;
import com.virnect.account.port.inbound.LicenseService;
import com.virnect.account.port.outbound.LicenseAttributeRepository;
import com.virnect.account.port.outbound.LicenseRepository;
import com.virnect.account.port.outbound.LicenseRevisionRepository;
import com.virnect.account.port.outbound.ProductRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {
	private final LicenseGradeService licenseGradeService;

	private final LicenseRepository licenseRepository;
	private final ProductRepository productRepository;
	private final LicenseAttributeRepository licenseAttributeRepository;
	private final LicenseRevisionRepository licenseRevisionRepository;

	@Transactional(readOnly = true)
	@Override
	public LicenseResponseDto getLicense(Long licenseId) {
		return licenseRepository.getLicenseResponse(licenseId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE));
	}

	@Override
	public void create(LicenseRequestDto licenseRequestDto) {
		Product product = productRepository.getProduct(licenseRequestDto.getProductId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

		if (!ApprovalStatus.APPROVED.equals(product.getStatus())) {
			throw new CustomException(NOT_FOUND_PRODUCT);
		}

		final ProductType productType = product.getProductType();

		checkLicenseGenerationRules(licenseRequestDto, productType);

		License newLicense = License.from(licenseRequestDto, product.getId());

		licenseRepository.save(newLicense);

		if (productType.isProductTypeWithAttribute()) {
			saveLicenseAttributes(newLicense.getId(), licenseRequestDto);
		}
	}

	private void saveLicenseAttributes(Long licenseId, LicenseRequestDto licenseRequestDto) {
		for (LicenseAttributeRequestDto licenseAttributeRequestDto : licenseRequestDto.getLicenseAttributes()) {
			LicenseAttribute newLicenseAttribute = LicenseAttribute.of(licenseId, licenseAttributeRequestDto);
			licenseAttributeRepository.save(newLicenseAttribute);
		}

		for (LicenseAdditionalAttributeRequestDto licenseAdditionalAttribute : licenseRequestDto.getLicenseAdditionalAttributes()) {
			LicenseAttribute newLicenseAttribute = LicenseAttribute.of(licenseId, licenseAdditionalAttribute);
			licenseAttributeRepository.save(newLicenseAttribute);
		}
	}

	private void checkLicenseGenerationRules(LicenseRequestDto licenseRequestDto, ProductType productType) {
		List<LicenseAttributeRequestDto> licenseAttributes = licenseRequestDto.getLicenseAttributes();
		List<LicenseAdditionalAttributeRequestDto> licenseAdditionalAttributes = licenseRequestDto.getLicenseAdditionalAttributes();

		validateLicenseGenerationRulesByProductType(productType, licenseAttributes, licenseAdditionalAttributes);

		LicenseGrade licenseGrade = licenseGradeService.getAvailableLicenseGradeForSale(
			licenseRequestDto.getLicenseGradeId());

		if (productType.isTrack() && !licenseGrade.getGradeType().isFree()) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}
	}

	private void validateLicenseGenerationRulesByProductType(
		ProductType productType, List<LicenseAttributeRequestDto> licenseAttributes,
		List<LicenseAdditionalAttributeRequestDto> licenseAdditionalAttributes
	) {
		if (productType.isTrack()) {
			if (!CollectionUtils.isEmpty(licenseAttributes) || !CollectionUtils.isEmpty(licenseAdditionalAttributes)) {
				throw new CustomException(INVALID_INPUT_VALUE);
			}
		}

		if (productType.isSquars()) {
			List<LicenseAttributeType> licenseAttributeTypes = licenseAttributes.stream()
				.map(LicenseAttributeRequestDto::getLicenseAttributeType)
				.collect(Collectors.toList());

			validateLicenseAttributes(licenseAttributes);

			validateIsContainingEssentialAttributeTypes(licenseAttributeTypes);

			checkAttributeMeaningDuplicate(licenseAttributes);

			validateIsContainingSquarsEssentialAttributeTypes(licenseAttributeTypes);

			validateLicenseAdditionalAttributes(licenseAdditionalAttributes);

			validateIsContainingSquarsAdditionalAttributeTypes(licenseAdditionalAttributes);
		}
	}

	private void validateLicenseAttributes(List<LicenseAttributeRequestDto> licenseAttributes) {
		boolean isValidAttributeReqeust =
			licenseAttributes != null
				&& !CollectionUtils.isEmpty(licenseAttributes)
				&& licenseAttributes.stream().allMatch(LicenseAttributeRequestDto::isDataTypeValid)
				&& licenseAttributes.stream().allMatch(LicenseAttributeRequestDto::isDataValueValid)
				&& licenseAttributes.stream().distinct().count() == licenseAttributes.size();

		if (!isValidAttributeReqeust) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}
	}

	private void validateLicenseAdditionalAttributes(
		List<LicenseAdditionalAttributeRequestDto> licenseAdditionalAttributes
	) {
		boolean isValidAdditionalAttributeRequest = licenseAdditionalAttributes != null
			&& !CollectionUtils.isEmpty(licenseAdditionalAttributes)
			&& licenseAdditionalAttributes.stream().allMatch(LicenseAdditionalAttributeRequestDto::isValid)
			&& licenseAdditionalAttributes.stream().allMatch(LicenseAdditionalAttributeRequestDto::isDataTypeValid)
			&& licenseAdditionalAttributes.stream().allMatch(LicenseAdditionalAttributeRequestDto::isDataValueValid)
			&& licenseAdditionalAttributes.stream().distinct().count() == licenseAdditionalAttributes.size();

		if (!isValidAdditionalAttributeRequest) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}
	}

	private void validateIsContainingEssentialAttributeTypes(List<LicenseAttributeType> licenseAttributeTypes) {
		boolean containsEssentialAttributeTypes =
			licenseAttributeTypes.contains(LicenseAttributeType.STORAGE_SIZE_PER_MB)
				&& licenseAttributeTypes.contains(LicenseAttributeType.MAXIMUM_WORKSPACE)
				&& licenseAttributeTypes.contains(LicenseAttributeType.MAXIMUM_GROUP)
				&& licenseAttributeTypes.contains(LicenseAttributeType.MAXIMUM_GROUP_USER);

		if (!containsEssentialAttributeTypes) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}
	}

	private void checkAttributeMeaningDuplicate(List<LicenseAttributeRequestDto> licenseAttributeRequestDtos) {
		LicenseAttributeRequestDto attributeOfMaximumProject = null;
		LicenseAttributeRequestDto attributeOfMaximumPublishProject = null;
		for (LicenseAttributeRequestDto licenseAttributeRequestDto : licenseAttributeRequestDtos) {
			if (LicenseAttributeType.MAXIMUM_PROJECT.equals(licenseAttributeRequestDto.getLicenseAttributeType())) {
				attributeOfMaximumProject = licenseAttributeRequestDto;
			}
			if (LicenseAttributeType.MAXIMUM_PUBLISHING_PROJECT.equals(
				licenseAttributeRequestDto.getLicenseAttributeType())) {
				attributeOfMaximumPublishProject = licenseAttributeRequestDto;
			}
		}

		if (attributeOfMaximumProject != null && attributeOfMaximumPublishProject != null) {
			if (Long.parseLong(attributeOfMaximumProject.getDataValue()) < Long.parseLong(
				attributeOfMaximumPublishProject.getDataValue())) {
				throw new CustomException(INVALID_INPUT_VALUE);
			}
		}
	}

	private void validateIsContainingSquarsAdditionalAttributeTypes(
		List<LicenseAdditionalAttributeRequestDto> licenseAdditionalAttributeRequestDtos
	) {
		long squarsAdditionalAttributesSize = LicenseAdditionalAttributeType.squarsAdditionalAttributesSize();
		long requestSquarsAdditionalAttributesSize = 0;

		for (LicenseAdditionalAttributeRequestDto licenseAdditionalAttributeRequestDto : licenseAdditionalAttributeRequestDtos) {
			if (licenseAdditionalAttributeRequestDto.getLicenseAdditionalAttributeType()
				.isSquarsAdditionalAttribute()) {
				requestSquarsAdditionalAttributesSize++;
			}
		}
		boolean isContainsAllSquarsAdditionalAttributes =
			squarsAdditionalAttributesSize == requestSquarsAdditionalAttributesSize;

		if (!isContainsAllSquarsAdditionalAttributes) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}
	}

	private void validateIsContainingSquarsEssentialAttributeTypes(List<LicenseAttributeType> licenseAttributeTypes) {
		long squarsAttributesSize = LicenseAttributeType.squarsAttributesSize();
		long requestSquarsAttributesSize = 0;

		for (LicenseAttributeType licenseAttributeType : licenseAttributeTypes) {
			if (licenseAttributeType.isSquarsAttribute()) {
				requestSquarsAttributesSize++;
			}
		}
		boolean isContainsAllSquarsAttributes = squarsAttributesSize == requestSquarsAttributesSize;

		if (!isContainsAllSquarsAttributes) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<LicenseResponseDto> getLicenses(
		LicenseSearchDto licenseSearchDto, Pageable pageable
	) {
		Page<LicenseResponseDto> licenses = licenseRepository.getLicensesResponse(licenseSearchDto, pageable);
		return new PageContentResponseDto<>(licenses, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<LicenseRevisionResponseDto> getLicenseRevisions(
		Long licenseId, Pageable pageable
	) {
		Page<LicenseRevisionResponseDto> licenses = licenseRevisionRepository.getLicenseRevisionResponses(
			licenseId, pageable);
		return new PageContentResponseDto<>(licenses, pageable);
	}

	@Override
	public void update(Long licenseId, LicenseRequestDto licenseRequestDto) {
		License license = getAvailableLicense(licenseId);

		if (license.getStatus().isImmutableStatus()) {
			throw new CustomException(INVALID_STATUS);
		}

		Product requestProduct = productRepository.getProduct(licenseRequestDto.getProductId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

		if (requestProduct.getStatus().isNotApproved()) {
			throw new CustomException(INVALID_PRODUCT);
		}

		checkLicenseGenerationRules(licenseRequestDto, requestProduct.getProductType());

		updateLicenseAttributes(license, licenseRequestDto.getLicenseAttributes());

		updateLicenseAdditionalAttributes(license, licenseRequestDto.getLicenseAdditionalAttributes());

		license.update(licenseRequestDto, requestProduct.getId());
	}

	private void updateLicenseAdditionalAttributes(
		License license, List<LicenseAdditionalAttributeRequestDto> requestLicenseAdditionalAttributes
	) {
		if (CollectionUtils.isEmpty(requestLicenseAdditionalAttributes)) {
			deleteUsingLicenseAdditionalAttributes(license.getId());
			return;
		}

		for (LicenseAdditionalAttributeRequestDto requestLicenseAdditionalAttribute : requestLicenseAdditionalAttributes) {
			findFirstLicenseAdditionalAttribute(
				license.getId(), requestLicenseAdditionalAttribute.getLicenseAdditionalAttributeType())
				.ifPresentOrElse(licenseAdditionalAttribute -> {
					licenseAdditionalAttribute.setStatus(UseStatus.USE);
					licenseAdditionalAttribute.setDataValue(requestLicenseAdditionalAttribute.getDataValue());
				}, () -> {
					LicenseAttribute licenseAdditionalAttribute = LicenseAttribute.of(
						license.getId(), requestLicenseAdditionalAttribute);
					licenseAttributeRepository.save(licenseAdditionalAttribute);
				});
		}
	}

	private void deleteUsingLicenseAdditionalAttributes(Long licenseId) {
		List<LicenseAttribute> licenseAdditionalAttributes = licenseAttributeRepository.getLicenseAttributes(
			licenseId, UseStatus.USE, DependencyType.INDEPENDENCE);
		licenseAdditionalAttributes.forEach(
			licenseAdditionalAttribute -> licenseAdditionalAttribute.setStatus(UseStatus.DELETE));
	}

	private void updateLicenseAttributes(License license, List<LicenseAttributeRequestDto> requestLicenseAttributes) {

		if (CollectionUtils.isEmpty(requestLicenseAttributes)) {
			deleteUsingLicenseAttributes(license.getId());
			return;
		}

		for (LicenseAttributeRequestDto requestLicenseAttribute : requestLicenseAttributes) {
			findFirstLicenseAttribute(license.getId(), requestLicenseAttribute.getLicenseAttributeType())
				.ifPresentOrElse(licenseAttribute -> {
					licenseAttribute.setStatus(UseStatus.USE);
					licenseAttribute.setDataValue(requestLicenseAttribute.getDataValue());
				}, () -> {
					LicenseAttribute licenseAttribute = LicenseAttribute.of(license.getId(), requestLicenseAttribute);
					licenseAttributeRepository.save(licenseAttribute);
				});
		}
	}

	private void deleteUsingLicenseAttributes(Long licenseId) {
		List<LicenseAttribute> licenseAttributes = licenseAttributeRepository.getLicenseAttributes(
			licenseId, UseStatus.USE, DependencyType.DEPENDENCE);
		licenseAttributes.forEach(licenseAttribute -> licenseAttribute.setStatus(UseStatus.DELETE));
	}

	private Optional<LicenseAttribute> findFirstLicenseAdditionalAttribute(
		Long licenseId, LicenseAdditionalAttributeType licenseAdditionalAttributeType
	) {
		List<LicenseAttribute> licenseAttributes = licenseAttributeRepository.getLicenseAttributes(
			licenseId, DependencyType.INDEPENDENCE);

		return licenseAttributes.stream()
			.filter(licenseAttribute -> licenseAttribute.getAdditionalAttributeType()
				.equals(licenseAdditionalAttributeType))
			.findFirst();
	}

	private Optional<LicenseAttribute> findFirstLicenseAttribute(
		Long licenseId, LicenseAttributeType licenseAttributeType
	) {
		List<LicenseAttribute> licenseAttributes = licenseAttributeRepository.getLicenseAttributes(
			licenseId, DependencyType.DEPENDENCE);

		return licenseAttributes.stream()
			.filter(licenseAttribute -> licenseAttribute.getAttributeType().equals(licenseAttributeType))
			.findFirst();
	}

	@Override
	public void updateByStatus(Long licenseId, ApprovalStatus approvalStatus) {
		License license = getAvailableLicense(licenseId);

		if (license.getStatus().isImmutableStatus()) {
			throw new CustomException(INVALID_STATUS);
		}

		if (!license.getStatus().equals(approvalStatus)) {
			license.updateApprovalStatus(approvalStatus);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public License getAvailableLicense(Long licenseId) {
		License license = licenseRepository.getLicense(licenseId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE));

		if (license.getUseStatus().isDelete()) {
			throw new CustomException(NOT_FOUND_LICENSE);
		}
		return license;
	}
}
