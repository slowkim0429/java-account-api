package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseGradeSearchDto;
import com.virnect.account.adapter.inbound.dto.response.grade.LicenseGradeResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.model.LicenseGrade;

public interface LicenseGradeRepositoryCustom {

	Optional<LicenseGradeResponseDto> getLicenseGradeResponse(Long gradeId);

	Page<LicenseGradeResponseDto> getLicenseGradeResponses(
		LicenseGradeSearchDto licenseGradeSearchDto, PageRequest pageable
	);

	Optional<LicenseGrade> getLicenseGrade(Long gradeId);

	Optional<LicenseGrade> getLicenseGrade(String licenseGradeName, ApprovalStatus approvalStatus);
}
