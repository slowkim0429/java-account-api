package com.virnect.account.port.outbound;

import java.util.List;
import java.util.Optional;

import com.virnect.account.adapter.inbound.dto.response.LicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeResponseDto;
import com.virnect.account.domain.enumclass.DependencyType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.LicenseAttribute;

public interface LicenseAttributeRepositoryCustom {
	List<LicenseAttributeResponseDto> getLicenseAttributeResponse(Long licenseId);

	List<LicenseAdditionalAttributeResponseDto> getLicenseAdditionalAttributeResponse(Long licenseId);

	List<LicenseAttribute> getLicenseAttributes(Long licenseId, DependencyType dependencyType);

	Optional<LicenseAttribute> getLicenseAttributeById(Long licenseAttributeId);

	List<LicenseAttribute> getLicenseAttributes(Long[] licenseIds, DependencyType dependencyType);

	List<LicenseAttribute> getLicenseAttributes(Long licenseId, UseStatus useStatus, DependencyType dependencyType);
}
