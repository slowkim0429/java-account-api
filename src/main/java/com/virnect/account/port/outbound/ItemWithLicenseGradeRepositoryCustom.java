package com.virnect.account.port.outbound;

import java.util.Optional;

import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseGradeResponseDto;

public interface ItemWithLicenseGradeRepositoryCustom {

	Optional<ItemAndLicenseGradeResponseDto> getItemAndLicenseResponse(Long itemId);
}
