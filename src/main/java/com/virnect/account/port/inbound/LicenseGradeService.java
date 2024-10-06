package com.virnect.account.port.inbound;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseGradeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseGradeSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.grade.LicenseGradeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.grade.LicenseGradeRevisionResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.model.LicenseGrade;

public interface LicenseGradeService {
	void create(LicenseGradeRequestDto licenseGradeRequestDto);

	LicenseGradeResponseDto getLicenseGradeById(Long gradeId);

	void updateGrade(Long gradeId, LicenseGradeRequestDto requestDto);

	void updateStatus(Long gradeId, ApprovalStatus status);

	PageContentResponseDto<LicenseGradeResponseDto> getLicenseGrades(
		LicenseGradeSearchDto licenseGradeSearchDto, PageRequest of
	);

	LicenseGrade getAvailableLicenseGradeForSale(Long gradeId);

	LicenseGrade getAvailableLicenseGrade(Long gradeId);

	List<LicenseGradeRevisionResponseDto> getLicenseGradeRevisions(Long licenseGradeId, Pageable pageable);
}
