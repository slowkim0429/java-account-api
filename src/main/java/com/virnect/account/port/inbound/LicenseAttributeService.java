package com.virnect.account.port.inbound;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.response.LicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;

public interface LicenseAttributeService {

	List<LicenseAttributeResponseDto> getLicenseAttributeResponse(Long licenseId);

	List<LicenseAdditionalAttributeResponseDto> getLicenseAdditionalAttributeResponse(Long licenseId);

	PageContentResponseDto<LicenseAttributeRevisionResponseDto> getLicenseAttributeRevisions(
		Long licenseId, LicenseAttributeType licenseAttributeType, Pageable pageable
	);

	PageContentResponseDto<LicenseAttributeRevisionResponseDto> getLicenseAdditionalAttributeRevisions(
		Long licenseId, LicenseAdditionalAttributeType licenseAdditionalAttributeType, Pageable pageable
	);
}
