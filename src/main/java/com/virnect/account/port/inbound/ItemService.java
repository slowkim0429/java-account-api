package com.virnect.account.port.inbound;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.item.ItemExposureSearchDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemPaymentLinkRequestDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemPaymentLinkSearchDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemRequestDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndGradeWithLicenseAttributesResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseGradeAndLicenseAttributesResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemPaymentLinkResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.model.Item;

public interface ItemService {
	void update(Long itemId, ItemRequestDto itemRequestDto);

	Item getAvailableItem(Long itemId);

	void create(ItemRequestDto requestDto);

	void updateByStatus(Long itemId, ApprovalStatus status);

	void updateByExpose(Long itemId, boolean isExposed);

	void synchronizeItem(Long itemId);

	PageContentResponseDto<ItemRevisionResponseDto> getItemRevisions(Long itemId, Pageable pageable);

	void delete(Long itemId);

	ItemDetailResponseDto getItemDetailById(Long itemId);

	PageContentResponseDto<ItemAndLicenseResponseDto> getItemList(ItemSearchDto itemSearchDto, Pageable pageable);

	void createItemPaymentLink(Long itemId, ItemPaymentLinkRequestDto itemPaymentLinkRequestDto);

	ItemAndLicenseGradeAndLicenseAttributesResponseDto getItemAndLicenseGradeAndLicenseAttributesByItemId(Long itemId);

	void verifyItemPaymentRequestData(Long itemId);

	ItemResponseDto getAttributeItemIdByLicenseItemId(Long itemId);

	List<ItemAndGradeWithLicenseAttributesResponseDto> getExposedItems(ItemExposureSearchDto searchDto);

	PageContentResponseDto<ItemPaymentLinkResponseDto> getItemPaymentLinks(
		ItemPaymentLinkSearchDto itemPaymentLinkSearchDto, Pageable pageable
	);
}
