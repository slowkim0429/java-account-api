package com.virnect.account.port.outbound;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeRevisionResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;

@Repository
public interface LicenseAttributeRevisionRepository {
	Page<LicenseAttributeRevisionResponseDto> getLicenseAttributeRevisionResponses(
		Long licenseId, LicenseAttributeType licenseAttributeType, Pageable pageable
	);

	Page<LicenseAttributeRevisionResponseDto> getLicenseAdditionalAttributeRevisions(
		Long licenseId, LicenseAdditionalAttributeType licenseAdditionalAttributeType, Pageable pageable
	);
}
