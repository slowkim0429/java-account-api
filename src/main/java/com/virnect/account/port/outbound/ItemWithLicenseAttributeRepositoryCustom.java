package com.virnect.account.port.outbound;

import java.util.List;

import com.virnect.account.adapter.inbound.dto.response.AdditionalItemAndLicenseAttributeResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;

public interface ItemWithLicenseAttributeRepositoryCustom {
	List<AdditionalItemAndLicenseAttributeResponseDto> getItemAndLicenseAttributes(
		ApprovalStatus status, Long licenseId
	);
}
