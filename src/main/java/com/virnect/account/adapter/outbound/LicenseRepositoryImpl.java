package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QLicense.*;
import static com.virnect.account.domain.model.QLicenseGrade.*;
import static com.virnect.account.domain.model.QProduct.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseSearchDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QLicenseResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.License;
import com.virnect.account.port.outbound.LicenseRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class LicenseRepositoryImpl implements LicenseRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqId(Long id) {
		if (id == null || id <= 0) {
			return null;
		}
		return license.id.eq(id);
	}

	private BooleanExpression eqProductId(Long productId) {
		if (productId == null || productId <= 0) {
			return null;
		}
		return license.productId.eq(productId);
	}

	private BooleanExpression eqStatus(String status) {
		if (status == null || status.equals("")) {
			return null;
		}
		return license.status.eq(ApprovalStatus.valueOf(status));
	}

	private BooleanExpression likeLicenseName(String licenseName) {
		if (StringUtils.isBlank(licenseName)) {
			return null;
		}
		return license.name.like(licenseName + "%");
	}

	@Override
	public Optional<License> getLicense(Long id) {
		return Optional.ofNullable(
			queryFactory.selectFrom(license)
				.where(
					eqId(id)
				)
				.fetchOne());
	}

	@Override
	public Optional<LicenseResponseDto> getLicenseResponse(Long licenseId) {
		return Optional.ofNullable(
			queryFactory.select(new QLicenseResponseDto(
						license.id,
						license.productId,
						product.productType,
						product.name,
						license.licenseGradeId,
						licenseGrade.name,
						licenseGrade.gradeType,
						license.name,
						license.description,
						license.status,
						license.useStatus,
						license.salesTarget,
						license.createdBy,
						license.createdDate,
						license.lastModifiedBy,
						license.updatedDate
					)
				)
				.from(license)
				.join(licenseGrade).on(licenseGrade.id.eq(license.licenseGradeId))
				.join(product).on(license.productId.eq(product.id))
				.where(
					license.id.eq(licenseId),
					license.useStatus.ne(UseStatus.DELETE)
				)
				.fetchOne()
		);
	}

	@Override
	public Page<LicenseResponseDto> getLicensesResponse(LicenseSearchDto licenseSearchDto, Pageable pageable) {
		QueryResults<LicenseResponseDto> results = queryFactory
			.select(new QLicenseResponseDto(
					license.id,
					license.productId,
					product.productType,
					product.name,
					license.licenseGradeId,
					licenseGrade.name,
					licenseGrade.gradeType,
					license.name,
					license.description,
					license.status,
					license.useStatus,
					license.salesTarget,
					license.createdBy,
					license.createdDate,
					license.lastModifiedBy,
					license.updatedDate
				)
			)
			.from(license)
			.join(licenseGrade).on(licenseGrade.id.eq(license.licenseGradeId))
			.join(product).on(license.productId.eq(product.id))
			.where(
				license.useStatus.ne(UseStatus.DELETE),
				eqLicenseGradeId(licenseSearchDto.getLicenseGradeId()),
				eqStatus(licenseSearchDto.getStatus()),
				eqLicenseGradeType(licenseSearchDto.getLicenseGradeType()),
				eqId(licenseSearchDto.getLicenseId()),
				eqProductId(licenseSearchDto.getProductId()),
				likeLicenseName(licenseSearchDto.getLicenseName())
			)
			.orderBy(license.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(results.getResults(), pageable, results.getTotal());
	}

	private BooleanExpression eqLicenseGradeId(Long licenseGradeId) {
		if (null == licenseGradeId) {
			return null;
		}
		return licenseGrade.id.eq(licenseGradeId);
	}

	private BooleanExpression eqLicenseGradeType(String licenseGradeType) {
		if (StringUtils.isBlank(licenseGradeType)) {
			return null;
		}
		LicenseGradeType requestLicenseGradeType = LicenseGradeType.valueOf(licenseGradeType.toUpperCase());
		return licenseGrade.gradeType.eq(requestLicenseGradeType);
	}
}
