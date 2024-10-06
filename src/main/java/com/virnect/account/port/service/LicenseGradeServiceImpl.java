package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseGradeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseGradeSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.grade.LicenseGradeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.grade.LicenseGradeRevisionResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.model.LicenseGrade;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.inbound.LicenseGradeService;
import com.virnect.account.port.outbound.LicenseGradeRepository;
import com.virnect.account.port.outbound.LicenseGradeRevisionRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class LicenseGradeServiceImpl implements LicenseGradeService {
	private final LicenseGradeRepository licenseGradeRepository;
	private final LicenseGradeRevisionRepository licenseGradeRevisionRepository;

	@Transactional(readOnly = true)
	@Override
	public LicenseGradeResponseDto getLicenseGradeById(Long gradeId) {
		return licenseGradeRepository.getLicenseGradeResponse(gradeId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_GRADE)
			);
	}

	public void updateGrade(Long gradeId, LicenseGradeRequestDto licenseGradeRequestDto) {
		LicenseGrade licenseGrade = licenseGradeRepository.getLicenseGrade(gradeId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_GRADE));

		if (!licenseGrade.getStatus().isRegister()) {
			throw new CustomException(INVALID_STATUS);
		}

		licenseGrade.update(licenseGradeRequestDto);
	}

	public void create(LicenseGradeRequestDto licenseGradeRequestDto) {
		licenseGradeRepository.getLicenseGrade(licenseGradeRequestDto.getName(), ApprovalStatus.APPROVED)
			.ifPresent(licenseGrade -> {
				throw new CustomException(DUPLICATE_LICENSE_GRADE_NAME);
			});

		LicenseGrade licenseGrade = LicenseGrade.from(licenseGradeRequestDto);
		licenseGradeRepository.save(licenseGrade);
	}

	@Override
	public void updateStatus(Long gradeId, ApprovalStatus status) {
		LicenseGrade licenseGrade = licenseGradeRepository.findById(gradeId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_GRADE));

		if (!status.isImmutableStatus()) {
			throw new CustomException(INVALID_STATUS);
		}

		if (licenseGrade.getStatus().isImmutableStatus()) {
			throw new CustomException(INVALID_STATUS);
		}

		if (ApprovalStatus.APPROVED.equals(status)) {
			licenseGradeRepository.getLicenseGrade(licenseGrade.getName(), ApprovalStatus.APPROVED)
				.ifPresent(duplicateLicenseGrade -> {
					throw new CustomException(DUPLICATE_LICENSE_GRADE_NAME);
				});
		}

		if (!licenseGrade.getStatus().equals(status)) {
			licenseGrade.setStatus(status);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<LicenseGradeResponseDto> getLicenseGrades(
		LicenseGradeSearchDto licenseGradeSearchDto, PageRequest pageable
	) {
		Page<LicenseGradeResponseDto> licenseGrades = licenseGradeRepository.getLicenseGradeResponses(
			licenseGradeSearchDto, pageable);
		return new PageContentResponseDto<>(licenseGrades, pageable);
	}

	@Transactional(readOnly = true)
	@Override
	public LicenseGrade getAvailableLicenseGradeForSale(Long gradeId) {
		LicenseGrade licenseGrade = licenseGradeRepository.getLicenseGrade(gradeId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_GRADE));

		if (licenseGrade.getStatus().isNotApproved()) {
			throw new CustomException(INVALID_STATUS);
		}

		return licenseGrade;
	}

	@Transactional(readOnly = true)
	@Override
	public LicenseGrade getAvailableLicenseGrade(Long gradeId) {
		return licenseGradeRepository.getLicenseGrade(gradeId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_LICENSE_GRADE));
	}

	@Override
	public List<LicenseGradeRevisionResponseDto> getLicenseGradeRevisions(
		Long licenseGradeId, Pageable pageable
	) {
		return licenseGradeRevisionRepository.getLicenseGradeRevisionResponses(licenseGradeId, pageable);
	}
}
