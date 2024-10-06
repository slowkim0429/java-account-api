package com.virnect.account.port.outbound;

import java.util.List;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.response.grade.LicenseGradeRevisionResponseDto;

public interface LicenseGradeRevisionRepository {
	List<LicenseGradeRevisionResponseDto> getLicenseGradeRevisionResponses(Long licenseGradeId, Pageable pageable);
}

