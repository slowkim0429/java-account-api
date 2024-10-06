package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.account.adapter.inbound.dto.request.item.ItemExposureSearchDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemPaymentLinkRequestDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemPaymentLinkSearchDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemRequestDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemSearchDto;
import com.virnect.account.adapter.inbound.dto.response.AdditionalItemAndLicenseAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndGradeWithLicenseAttributesResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseGradeAndLicenseAttributesResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseGradeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemPaymentLinkResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemWithLicenseGradeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.outbound.request.ItemSendDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.DependencyType;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Item;
import com.virnect.account.domain.model.ItemPaymentLink;
import com.virnect.account.domain.model.License;
import com.virnect.account.domain.model.LicenseAttribute;
import com.virnect.account.domain.model.LicenseGrade;
import com.virnect.account.domain.model.OrganizationLicense;
import com.virnect.account.domain.model.Product;
import com.virnect.account.domain.model.User;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.ContractAPIService;
import com.virnect.account.port.inbound.EmailAuthService;
import com.virnect.account.port.inbound.ExternalServiceMappingService;
import com.virnect.account.port.inbound.ItemService;
import com.virnect.account.port.inbound.LicenseGradeService;
import com.virnect.account.port.inbound.LicenseService;
import com.virnect.account.port.inbound.OrganizationLicenseService;
import com.virnect.account.port.outbound.ItemPaymentLinkRepository;
import com.virnect.account.port.outbound.ItemRepository;
import com.virnect.account.port.outbound.ItemRevisionRepository;
import com.virnect.account.port.outbound.ItemWithLicenseAttributeRepository;
import com.virnect.account.port.outbound.ItemWithLicenseGradeRepository;
import com.virnect.account.port.outbound.LicenseAttributeRepository;
import com.virnect.account.port.outbound.LicenseGradeRepository;
import com.virnect.account.port.outbound.LicenseRepository;
import com.virnect.account.port.outbound.LicenseWithGradeRepository;
import com.virnect.account.port.outbound.ProductRepository;
import com.virnect.account.port.outbound.UserRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.util.ZonedDateTimeUtil;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItemServiceImpl implements ItemService {
	private final LicenseService licenseService;
	private final LicenseGradeService licenseGradeService;
	private final EmailAuthService emailAuthService;
	private final OrganizationLicenseService organizationLicenseService;

	private final ItemRepository itemRepository;
	private final ItemWithLicenseAttributeRepository itemWithLicenseAttributeRepository;
	private final ContractAPIService contractAPIService;
	private final LicenseAttributeRepository licenseAttributeRepository;
	private final ItemRevisionRepository itemRevisionRepository;
	private final ExternalServiceMappingService externalServiceMappingService;
	private final ItemPaymentLinkRepository itemPaymentLinkRepository;
	private final UserRepository userRepository;
	private final LicenseWithGradeRepository licenseWithGradeRepository;
	private final ItemWithLicenseGradeRepository itemWithLicenseGradeRepository;
	private final ProductRepository productRepository;
	private final LicenseGradeRepository licenseGradeRepository;
	private final LicenseRepository licenseRepository;

	@Override
	public void updateByStatus(Long itemId, ApprovalStatus status) {
		Item item = itemRepository.getItemById(itemId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ITEM));

		if (item.getStatus().isImmutableStatus()) {
			throw new CustomException(ErrorCode.INVALID_STATUS);
		}

		if (ItemType.ATTRIBUTE.equals(item.getItemType()) && status.isApproved()) {
			LicenseAttribute licenseAttribute = licenseAttributeRepository.getLicenseAttributeById(
					item.getLicenseAttributeId())
				.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_ATTRIBUTE));

			if (existsApprovedAttributeItem(item.getLicenseId(), licenseAttribute.getAdditionalAttributeType())) {
				throw new CustomException(ErrorCode.DUPLICATE_ATTRIBUTE_ITEM);
			}
		}

		item.setStatus(status);

		if (status.isApproved()) {
			try {
				externalServiceMappingService.createItem(item);
			} catch (FeignException e) {
				StringWriter stackTraceContent = new StringWriter();
				e.printStackTrace(new PrintWriter(stackTraceContent));
				log.error(stackTraceContent.toString());
			}
			synchronizeItem(item.getId());
		}
	}

	@Override
	public void synchronizeItem(Long itemId) {
		Item item = itemRepository.getItemById(itemId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));
		if (item.getStatus().isNotApproved()) {
			throw new CustomException(INVALID_ITEM_STATUS);
		}
		License license = licenseService.getAvailableLicense(item.getLicenseId());

		Product product = productRepository.getProduct(license.getProductId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

		if (!product.getProductType().isTargetOfSynchronization()) {
			return;
		}

		LicenseGrade licenseGrade = licenseGradeService.getAvailableLicenseGrade(license.getLicenseGradeId());

		ItemSendDto itemSendDto = ItemSendDto.of(item, license, licenseGrade);
		contractAPIService.syncItem(itemSendDto);
	}

	@Override
	public void updateByExpose(Long itemId, boolean isExposed) {
		Item requestItem = itemRepository.getItemById(itemId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

		if (!ApprovalStatus.APPROVED.equals(requestItem.getStatus())) {
			throw new CustomException(ErrorCode.INVALID_ITEM_STATUS);
		}

		LicenseGrade requestLicenseGrade = licenseWithGradeRepository.getLicenseGrade(requestItem.getLicenseId())
			.orElseThrow(() -> {
				throw new CustomException(ErrorCode.NOT_FOUND_LICENSE_GRADE);
			});

		if (requestItem.getIsExposed() == isExposed) {
			throw new CustomException(ErrorCode.INVALID_ITEM_EXPOSED);
		}

		if (requestLicenseGrade.getGradeType().equals(LicenseGradeType.ENTERPRISE)) {
			throw new CustomException(INVALID_ENTERPRISE_IS_EXPOSE);
		}

		if (!isExposed && LicenseGradeType.FREE_PLUS.equals(requestLicenseGrade.getGradeType())) {
			throw new CustomException(ErrorCode.INVALID_FREE_LICENSE_EXPOSED);
		}

		License license = licenseRepository.getLicense(requestItem.getLicenseId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE));

		if (isExposed) {
			itemRepository.getItem(
				license.getProductId(), requestItem.getItemType(), requestLicenseGrade.getGradeType(),
				requestItem.getRecurringInterval(), true
			).ifPresent(exposedItem -> {
				exposedItem.setIsExposed(false);
				synchronizeItem(exposedItem.getId());
			});
		}
		requestItem.setIsExposed(isExposed);
		synchronizeItem(requestItem.getId());
	}

	@Transactional(readOnly = true)
	@Override
	public ItemDetailResponseDto getItemDetailById(Long itemId) {
		final ItemDetailResponseDto itemDetailResponseDto = itemRepository.getItemDetailResponseDtoById(itemId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ITEM));

		if (ItemType.ATTRIBUTE.equals(itemDetailResponseDto.getItemType())) {
			LicenseAttribute licenseAttribute = licenseAttributeRepository.getLicenseAttributeById(
					itemDetailResponseDto.getLicenseAttributeId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LICENSE_ATTRIBUTE));

			itemDetailResponseDto.setAdditionalAttributeType(licenseAttribute.getAdditionalAttributeType());
			itemDetailResponseDto.setDataValue(licenseAttribute.getDataValue());
			itemDetailResponseDto.setDataType(licenseAttribute.getDataType());
		}

		return itemDetailResponseDto;
	}

	@Override
	public void create(ItemRequestDto itemRequestDto) {
		License license = licenseService.getAvailableLicense(itemRequestDto.getLicenseId());

		if (license.getStatus().isNotApproved()) {
			throw new CustomException(ErrorCode.INVALID_STATUS);
		}

		Product product = productRepository.getProduct(license.getProductId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

		ProductType productType = product.getProductType();

		LicenseGrade licenseGrade = licenseGradeRepository.getLicenseGrade(license.getLicenseGradeId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_GRADE));

		checkItemCommonGenerationRules(itemRequestDto, productType, licenseGrade.getGradeType());

		final ItemType itemType = itemRequestDto.itemTypeValueOf();

		if (ItemType.LICENSE.equals(itemType)) {
			createLicenseItem(itemRequestDto);
		} else if (ItemType.ATTRIBUTE.equals(itemType)) {
			createAttributeItem(itemRequestDto);
		}
	}

	private void checkItemCommonGenerationRules(
		ItemRequestDto itemRequestDto, ProductType productType, LicenseGradeType licenseGradeType
	) {
		if (productType.isTrack() && !licenseGradeType.isFree()) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}

		validateFreeLicenseGrade(
			itemRequestDto.itemTypeValueOf(), itemRequestDto.recurringIntervalTypeValueOf(), licenseGradeType
		);
	}

	private void createAttributeItem(ItemRequestDto itemRequestDto) {
		checkAttributeItemGenerationRules(itemRequestDto);

		final Item item = Item.createAttributeItem(itemRequestDto);
		itemRepository.save(item);
	}

	private void checkAttributeItemGenerationRules(ItemRequestDto itemRequestDto) {
		if (!itemRequestDto.getRecurringInterval().equals(RecurringIntervalType.NONE.name())) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}

		if (itemRequestDto.getMonthlyUsedAmount().compareTo(new BigDecimal("0")) != 0) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}

		if (itemRequestDto.getLicenseAttributeId() == null) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}

		LicenseAttribute licenseAttribute = licenseAttributeRepository.getLicenseAttributeById(
				itemRequestDto.getLicenseAttributeId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_ATTRIBUTE));

		if (!UseStatus.USE.equals(licenseAttribute.getStatus())) {
			throw new CustomException(INVALID_STATUS);
		}

		if (!DependencyType.INDEPENDENCE.equals(licenseAttribute.getAttributeDependencyType())
			|| licenseAttribute.getAdditionalAttributeType() == null) {
			throw new CustomException(ErrorCode.INVALID_LICENSE_ATTRIBUTE);
		}

		if (!licenseAttribute.getLicenseId().equals(itemRequestDto.getLicenseId())) {
			throw new CustomException(ErrorCode.INVALID_LICENSE_ATTRIBUTE);
		}

		if (existsApprovedAttributeItem(
			itemRequestDto.getLicenseId(), licenseAttribute.getAdditionalAttributeType())) {
			throw new CustomException(ErrorCode.DUPLICATE_ATTRIBUTE_ITEM);
		}
	}

	private boolean existsApprovedAttributeItem(
		Long licenseId, LicenseAdditionalAttributeType licenseAdditionalAttributeType
	) {
		Optional<Item> attributeItem = itemRepository.getItem(licenseId, licenseAdditionalAttributeType);

		return attributeItem.isPresent();
	}

	private void createLicenseItem(ItemRequestDto requestDto) {
		checkLicenseItemGenerationRules(requestDto);

		final Item item = Item.createLicenseItem(requestDto);
		itemRepository.save(item);
	}

	private void checkLicenseItemGenerationRules(ItemRequestDto requestDto) {
		if (requestDto.recurringIntervalTypeValueOf().isYear()) {
			checkYearMonthlyUsedAmount(requestDto.getMonthlyUsedAmount(), requestDto.getAmount());
		} else if (requestDto.recurringIntervalTypeValueOf().isMonth()) {
			checkMonthMonthlyUsedAmount(requestDto);
			validAmountIsNotZero(requestDto.getAmount());
			validAmountIsNotZero(requestDto.getMonthlyUsedAmount());
		} else if (requestDto.recurringIntervalTypeValueOf().isNone()) {
			validAmountIsZero(requestDto.getAmount());
			validAmountIsZero(requestDto.getMonthlyUsedAmount());
		}

		if (requestDto.getLicenseAttributeId() != null) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	@Override
	public void update(Long itemId, ItemRequestDto itemRequestDto) {
		Item item = getAvailableItem(itemId);

		if (item.getStatus().isImmutableStatus()) {
			throw new CustomException(INVALID_STATUS);
		}

		License license = licenseService.getAvailableLicense(itemRequestDto.getLicenseId());

		if (license.getStatus().isNotApproved()) {
			throw new CustomException(INVALID_STATUS);
		}

		Product product = productRepository.getProduct(license.getProductId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

		ProductType productType = product.getProductType();

		LicenseGrade licenseGrade = licenseGradeRepository.getLicenseGrade(license.getLicenseGradeId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_GRADE));

		checkItemCommonGenerationRules(itemRequestDto, productType, licenseGrade.getGradeType());

		final ItemType itemType = itemRequestDto.itemTypeValueOf();

		if (ItemType.LICENSE.equals(itemType)) {
			checkLicenseItemGenerationRules(itemRequestDto);
			item.update(itemRequestDto, PaymentType.SUBSCRIPTION);
		} else if (ItemType.ATTRIBUTE.equals(itemType)) {
			checkAttributeItemGenerationRules(itemRequestDto);
			item.update(itemRequestDto, PaymentType.NONRECURRING);
		}
	}

	private void validateFreeLicenseGrade(
		ItemType itemType, RecurringIntervalType recurringIntervalType, LicenseGradeType licenseGradeType
	) {
		if (licenseGradeType.isFree()) {
			if (itemType.isAttribute()) {
				throw new CustomException(INVALID_ITEM_TYPE);
			}

			if (recurringIntervalType.isNotNone()) {
				throw new CustomException(INVALID_RECURRING_INTERVAL_TYPE);
			}
		}
	}

	@Override
	public PageContentResponseDto<ItemRevisionResponseDto> getItemRevisions(
		Long itemId, Pageable pageable
	) {
		Page<ItemRevisionResponseDto> items = itemRevisionRepository.getItemRevisionResponses(
			itemId, pageable);
		return new PageContentResponseDto<>(items, pageable);
	}

	@Override
	public void delete(Long itemId) {
		Item item = getAvailableItem(itemId);

		if (item.getStatus().isNotRegister()) {
			throw new CustomException(INVALID_STATUS);
		}
		item.setUseStatus(UseStatus.DELETE);
	}

	@Override
	public PageContentResponseDto<ItemAndLicenseResponseDto> getItemList(
		ItemSearchDto itemSearchDto, Pageable pageable
	) {
		Page<ItemAndLicenseResponseDto> itemList = itemRepository.getItemList(itemSearchDto, pageable);
		return new PageContentResponseDto<>(itemList, pageable);
	}

	@Override
	public void createItemPaymentLink(Long itemId, ItemPaymentLinkRequestDto itemPaymentLinkRequestDto) {
		User user = userRepository.getUser(itemPaymentLinkRequestDto.getEmail())
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		Item item = itemRepository.getItemById(itemId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

		if (item.getStatus().isNotApproved()) {
			throw new CustomException(INVALID_STATUS);
		}

		License license = licenseService.getAvailableLicense(item.getLicenseId());

		LicenseGrade licenseGrade = licenseGradeService.getAvailableLicenseGrade(license.getLicenseGradeId());

		if (!LicenseGradeType.ENTERPRISE.equals(licenseGrade.getGradeType())) {
			throw new CustomException(INVALID_INPUT_VALUE);
		}

		if (ItemType.ATTRIBUTE.equals(item.getItemType())) {
			OrganizationLicense organizationLicense = organizationLicenseService.getOrganizationLicenseInProgressByProductType(
				user.getOrganizationId(), ProductType.SQUARS);

			if (!item.getLicenseId().equals(organizationLicense.getLicenseId())) {
				throw new CustomException(NOT_FOUND_ORGANIZATION_LICENSE);
			}
		}

		ItemPaymentLink itemPaymentLink = ItemPaymentLink.of(
			itemId, user.getId(), itemPaymentLinkRequestDto, ZonedDateTimeUtil.getInfinitePeriod());
		itemPaymentLinkRepository.save(itemPaymentLink);

		emailAuthService.sendItemPaymentLinkEmail(user, item);
	}

	@Override
	public ItemAndLicenseGradeAndLicenseAttributesResponseDto getItemAndLicenseGradeAndLicenseAttributesByItemId(
		Long itemId
	) {
		ItemAndLicenseGradeResponseDto itemAndLicenseGradeResponse = itemWithLicenseGradeRepository.getItemAndLicenseResponse(
			itemId).orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

		ItemAndLicenseGradeAndLicenseAttributesResponseDto itemAndLicenseGradeAndLicenseAttributesResponseDto = new ItemAndLicenseGradeAndLicenseAttributesResponseDto(
			itemAndLicenseGradeResponse);

		if (ItemType.ATTRIBUTE.equals(itemAndLicenseGradeResponse.getItemType())) {
			List<LicenseAdditionalAttributeResponseDto> licenseAdditionalAttributeResponseDtos = licenseAttributeRepository.getLicenseAdditionalAttributeResponse(
				itemAndLicenseGradeResponse.getLicenseId());
			itemAndLicenseGradeAndLicenseAttributesResponseDto.setLicenseAdditionalAttributes(
				licenseAdditionalAttributeResponseDtos);
		} else if (ItemType.LICENSE.equals(itemAndLicenseGradeResponse.getItemType())) {
			List<LicenseAttributeResponseDto> licenseAttributeResponseDtos = licenseAttributeRepository.getLicenseAttributeResponse(
				itemAndLicenseGradeResponse.getLicenseId());
			itemAndLicenseGradeAndLicenseAttributesResponseDto.setLicenseAttributes(licenseAttributeResponseDtos);

			itemRepository.getItem(itemAndLicenseGradeResponse.getLicenseId(), ItemType.ATTRIBUTE)
				.ifPresent(additionalItem -> {
					List<AdditionalItemAndLicenseAttributeResponseDto> additionalItemAndLicenseAttributeResponse = itemWithLicenseAttributeRepository.getItemAndLicenseAttributes(
						ApprovalStatus.APPROVED, itemAndLicenseGradeResponse.getLicenseId()
					);
					itemAndLicenseGradeAndLicenseAttributesResponseDto.setAdditionalItems(
						additionalItemAndLicenseAttributeResponse);
				});
		}

		if (itemAndLicenseGradeResponse.getRecurringInterval().isYear()) {
			BigDecimal annuallyAmount = getAnnuallyAmount(itemAndLicenseGradeResponse.getMonthlyUsedAmount());
			itemAndLicenseGradeAndLicenseAttributesResponseDto.setAnnuallyAmount(annuallyAmount);

			BigDecimal discountAmount = getDiscountAmount(annuallyAmount, itemAndLicenseGradeResponse.getAmount());
			itemAndLicenseGradeAndLicenseAttributesResponseDto.setDiscountAmount(discountAmount);
		}

		return itemAndLicenseGradeAndLicenseAttributesResponseDto;
	}

	@Override
	public void verifyItemPaymentRequestData(Long itemId) {
		final Long currentUserId = SecurityUtil.getCurrentUserId();

		ItemPaymentLink requestItemPaymentLink = itemPaymentLinkRepository.getItemPaymentLink(currentUserId, itemId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ITEM_PAYMENT_LINK));

		if (isExpired(requestItemPaymentLink.getExpiredDate())) {
			throw new CustomException(ErrorCode.NOT_FOUND_ITEM_PAYMENT_LINK);
		}
	}

	@Override
	public ItemResponseDto getAttributeItemIdByLicenseItemId(Long itemId) {
		Item requestItem = itemRepository.getItemById(itemId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

		Item attributeItem = itemRepository.getItem(requestItem.getLicenseId(), ItemType.ATTRIBUTE)
			.orElseThrow(() -> new CustomException(NOT_FOUND_ATTRIBUTE_ITEM));

		return ItemResponseDto.from(attributeItem.getId());
	}

	private boolean isExpired(ZonedDateTime expiredAt) {
		return ZonedDateTimeUtil.zoneOffsetOfUTC().isAfter(expiredAt);
	}

	@Override
	public Item getAvailableItem(Long itemId) {
		return itemRepository.getItemById(itemId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ITEM));
	}

	private void checkYearMonthlyUsedAmount(BigDecimal monthlyUsedAmount, BigDecimal amount) {
		if ((monthlyUsedAmount.multiply(new BigDecimal("12"))).compareTo(amount) <= 0) {
			throw new CustomException(ErrorCode.INVALID_MONTHLY_USED_AMOUNT);
		}

		if (monthlyUsedAmount.compareTo(amount) >= 0) {
			throw new CustomException(INVALID_AMOUNT_IS_LESS_THAN_OR_EQUAL_TO_MONTHLY_USED_AMOUNT);
		}
	}

	private void checkMonthMonthlyUsedAmount(ItemRequestDto itemRequestDto) {
		if (!itemRequestDto.getMonthlyUsedAmount().equals(itemRequestDto.getAmount())) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	private void validAmountIsZero(BigDecimal amount) {
		if (amount.compareTo(new BigDecimal("0")) != 0) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	private void validAmountIsNotZero(BigDecimal amount) {
		if (amount.compareTo(new BigDecimal("0")) == 0) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	@Override
	public List<ItemAndGradeWithLicenseAttributesResponseDto> getExposedItems(ItemExposureSearchDto searchDto) {
		List<ItemWithLicenseGradeResponseDto> itemsByExposed = itemRepository.getItemResponses(
			ApprovalStatus.APPROVED, true, searchDto.getRecurringInterval(), ItemType.LICENSE, ProductType.SQUARS
		);

		List<ItemAndGradeWithLicenseAttributesResponseDto> resultDto = new ArrayList<>();

		Long[] licenseIds = itemsByExposed.stream()
			.map(ItemWithLicenseGradeResponseDto::getLicenseId)
			.collect(Collectors.toList())
			.toArray(Long[]::new);

		List<LicenseAttribute> licenseAttributes = licenseAttributeRepository.getLicenseAttributes(
			licenseIds, DependencyType.DEPENDENCE);

		for (ItemWithLicenseGradeResponseDto itemWithLicenseGradeResponseDto : itemsByExposed) {

			List<LicenseAttribute> attributes = licenseAttributes.stream()
				.filter(licenseAttribute -> licenseAttribute.getLicenseId()
					.equals(itemWithLicenseGradeResponseDto.getLicenseId()))
				.collect(Collectors.toList());

			ItemAndGradeWithLicenseAttributesResponseDto itemWithLicenseAttributesExposureDto = new ItemAndGradeWithLicenseAttributesResponseDto(
				itemWithLicenseGradeResponseDto);

			itemWithLicenseAttributesExposureDto.setLicenseAttributes(attributes);
			List<AdditionalItemAndLicenseAttributeResponseDto> additionalItemAndLicenseAttributeResponse = itemWithLicenseAttributeRepository.getItemAndLicenseAttributes(
				ApprovalStatus.APPROVED, itemWithLicenseGradeResponseDto.getLicenseId()
			);

			itemWithLicenseAttributesExposureDto.setAdditionalLicenseAttributes(
				additionalItemAndLicenseAttributeResponse);

			if (itemWithLicenseAttributesExposureDto.getRecurringInterval().isYear()) {
				BigDecimal annuallyAmount = getAnnuallyAmount(
					itemWithLicenseAttributesExposureDto.getMonthlyUsedAmount());
				itemWithLicenseAttributesExposureDto.setAnnuallyAmount(annuallyAmount);

				BigDecimal discountAmount = getDiscountAmount(
					annuallyAmount, itemWithLicenseAttributesExposureDto.getAmount());
				itemWithLicenseAttributesExposureDto.setDiscountAmount(discountAmount);
			}

			resultDto.add(itemWithLicenseAttributesExposureDto);
		}

		return resultDto;
	}

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<ItemPaymentLinkResponseDto> getItemPaymentLinks(
		ItemPaymentLinkSearchDto itemPaymentLinkSearchDto, Pageable pageable
	) {
		Page<ItemPaymentLinkResponseDto> itemPaymentLinkResponses = itemPaymentLinkRepository.getItemPaymentLinkResponses(
			itemPaymentLinkSearchDto, pageable);
		return new PageContentResponseDto<>(itemPaymentLinkResponses, pageable);
	}

	private BigDecimal getDiscountAmount(BigDecimal annuallyAmount, BigDecimal amount) {
		return annuallyAmount.subtract(amount);
	}

	private BigDecimal getAnnuallyAmount(BigDecimal monthlyUsedAmount) {
		BigDecimal monthOfYear = new BigDecimal(12);
		return monthlyUsedAmount.multiply(monthOfYear);
	}

}
