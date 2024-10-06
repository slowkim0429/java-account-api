package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.item.ItemPaymentLinkSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ItemPaymentLinkResponseDto;
import com.virnect.account.domain.model.ItemPaymentLink;

public interface ItemPaymentLinkRepositoryCustom {
	Optional<ItemPaymentLink> getItemPaymentLink(Long userId, Long itemId);

	Page<ItemPaymentLinkResponseDto> getItemPaymentLinkResponses(
		ItemPaymentLinkSearchDto itemPaymentLinkSearchDto, Pageable pageable
	);
}

