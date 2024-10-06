package com.virnect.account.port.outbound;

import java.util.Optional;

import com.virnect.account.domain.model.LicenseGrade;

public interface LicenseWithGradeRepositoryCustom {

	Optional<LicenseGrade> getLicenseGrade(Long licenseId);
}
