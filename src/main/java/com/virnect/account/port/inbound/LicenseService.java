package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseSearchDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.model.License;

public interface LicenseService {
	LicenseResponseDto getLicense(Long licenseId);

	void update(Long licenseId, LicenseRequestDto licenseRequestDto);

	License getAvailableLicense(Long licenseId);

	void create(LicenseRequestDto licenseRequestDto);

	void updateByStatus(Long licenseId, ApprovalStatus status);

	PageContentResponseDto<LicenseResponseDto> getLicenses(LicenseSearchDto licenseSearchDto, Pageable pageable);

	PageContentResponseDto<LicenseRevisionResponseDto> getLicenseRevisions(Long licenseId, Pageable pageable);
}
