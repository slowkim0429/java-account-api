package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseSearchDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseResponseDto;
import com.virnect.account.domain.model.License;

public interface LicenseRepositoryCustom {
	Optional<LicenseResponseDto> getLicenseResponse(Long licenseId);

	Optional<License> getLicense(Long id);

	Page<LicenseResponseDto> getLicensesResponse(LicenseSearchDto licenseSearchDto, Pageable pageable);
}
