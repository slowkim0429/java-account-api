package com.virnect.account.port.outbound;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.item.ItemSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemWithLicenseGradeResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.model.Item;

public interface ItemRepositoryCustom {
	Optional<Item> getItemById(Long itemId);

	Optional<ItemDetailResponseDto> getItemDetailResponseDtoById(Long itemId);

	Page<ItemAndLicenseResponseDto> getItemList(ItemSearchDto itemSearchDto, Pageable pageable);

	Optional<Item> getItem(Long licenseId, ItemType itemType);

	Optional<Item> getItem(
		LicenseGradeType licenseGradeType, ItemType itemType, Boolean isExposed, ProductType productType
	);

	Optional<Item> getItem(Long licenseId, LicenseAdditionalAttributeType additionalAttributeType);

	Optional<Item> getItem(
		Long productId, ItemType itemType, LicenseGradeType licenseGradeType,
		RecurringIntervalType recurringIntervalType, Boolean isExposed
	);

	List<ItemWithLicenseGradeResponseDto> getItemResponses(
		ApprovalStatus itemApprovalStatus, boolean isExposed, RecurringIntervalType recurringIntervalType,
		ItemType itemType, ProductType productType
	);
}
