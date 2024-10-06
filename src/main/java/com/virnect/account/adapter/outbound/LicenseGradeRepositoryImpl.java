package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QLicenseGrade.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseGradeSearchDto;
import com.virnect.account.adapter.inbound.dto.response.grade.LicenseGradeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.grade.QLicenseGradeResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.model.LicenseGrade;
import com.virnect.account.port.outbound.LicenseGradeRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class LicenseGradeRepositoryImpl implements LicenseGradeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqId(Long id) {
		if (id == null || id <= 0) {
			return null;
		}
		return licenseGrade.id.eq(id);
	}

	private BooleanExpression eqStatus(ApprovalStatus status) {
		if (status == null) {
			return null;
		}
		return licenseGrade.status.eq(status);
	}

	private BooleanExpression eqGradeType(LicenseGradeType gradeType) {
		if (gradeType == null) {
			return null;
		}
		return licenseGrade.gradeType.eq(gradeType);
	}

	@Override
	public Page<LicenseGradeResponseDto> getLicenseGradeResponses(
		LicenseGradeSearchDto licenseGradeSearchDto, PageRequest pageable
	) {
		QueryResults<LicenseGradeResponseDto> pageResult = queryFactory
			.select(new QLicenseGradeResponseDto(
				licenseGrade.id,
				licenseGrade.gradeType,
				licenseGrade.name,
				licenseGrade.description,
				licenseGrade.status,
				licenseGrade.createdDate,
				licenseGrade.updatedDate,
				licenseGrade.createdBy,
				licenseGrade.lastModifiedBy
			))
			.from(licenseGrade)
			.where(
				eqStatus(licenseGradeSearchDto.getStatus()),
				eqGradeType(licenseGradeSearchDto.getGradeType())
			)
			.orderBy(licenseGrade.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(pageResult.getResults(), pageable, pageResult.getTotal());
	}

	@Override
	public Optional<LicenseGradeResponseDto> getLicenseGradeResponse(Long id) {
		return Optional.ofNullable(
			queryFactory
				.select(new QLicenseGradeResponseDto(
					licenseGrade.id,
					licenseGrade.gradeType,
					licenseGrade.name,
					licenseGrade.description,
					licenseGrade.status,
					licenseGrade.createdDate,
					licenseGrade.updatedDate,
					licenseGrade.createdBy,
					licenseGrade.lastModifiedBy
				))
				.from(licenseGrade)
				.where(
					eqId(id)
				)
				.fetchOne());
	}

	@Override
	public Optional<LicenseGrade> getLicenseGrade(Long id) {
		return Optional.ofNullable(
			queryFactory.selectFrom(licenseGrade)
				.where(
					eqId(id)
				)
				.fetchOne());
	}

	@Override
	public Optional<LicenseGrade> getLicenseGrade(
		String licenseGradeName, ApprovalStatus approvalStatus
	) {
		return Optional.ofNullable(
			queryFactory.selectFrom(licenseGrade)
				.where(
					licenseGrade.name.eq(licenseGradeName),
					licenseGrade.status.eq(approvalStatus)
				)
				.fetchFirst()
		);
	}
}
