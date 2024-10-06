package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QItem.*;
import static com.virnect.account.domain.model.QLicense.*;
import static com.virnect.account.domain.model.QLicenseGrade.*;
import static com.virnect.account.domain.model.QOrganizationLicense.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseSearchDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QOrganizationLicenseResponseDto;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.model.OrganizationLicense;
import com.virnect.account.port.outbound.OrganizationLicenseRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class OrganizationLicenseRepositoryImpl implements OrganizationLicenseRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqId(Long id) {
		if (id == null || id <= 0) {
			return null;
		}
		return organizationLicense.id.eq(id);
	}

	private BooleanExpression eqOrganizationId(Long organizationId) {
		if (organizationId == null || organizationId <= 0) {
			return null;
		}
		return organizationLicense.organizationId.eq(organizationId);
	}

	private BooleanExpression eqLicenseId(Long licenseId) {
		if (licenseId == null || licenseId <= 0) {
			return null;
		}
		return organizationLicense.licenseId.eq(licenseId);
	}

	private BooleanExpression eqLicenseGradeId(Long licenseGradeId) {
		if (licenseGradeId == null || licenseGradeId <= 0) {
			return null;
		}
		return organizationLicense.licenseGradeId.eq(licenseGradeId);
	}

	private BooleanExpression eqProductId(Long productId) {
		if (productId == null || productId <= 0) {
			return null;
		}
		return organizationLicense.productId.eq(productId);
	}

	private BooleanExpression eqProductType(ProductType productType) {
		if (productType == null) {
			return null;
		}
		return organizationLicense.productType.eq(productType);
	}

	private BooleanExpression eqLicenseGradeType(LicenseGradeType licenseGradeType) {
		if (licenseGradeType == null) {
			return null;
		}
		return organizationLicense.licenseGradeType.eq(licenseGradeType);
	}

	private BooleanExpression eqContractId(Long contractId) {
		if (contractId == null || contractId <= 0) {
			return null;
		}
		return organizationLicense.contractId.eq(contractId);
	}

	private BooleanExpression eqStatus(OrganizationLicenseStatus status) {
		if (status == null) {
			return null;
		}
		return organizationLicense.status.eq(status);
	}

	@Override
	public Optional<OrganizationLicense> getOrganizationLicense(Long id) {
		return Optional.ofNullable(
			queryFactory.selectFrom(organizationLicense)
				.where(
					eqId(id)
				)
				.fetchOne());
	}

	@Override
	public Optional<OrganizationLicense> getOrganizationLicense(
		Long organizationId, Long contractId
	) {
		return Optional.ofNullable(
			queryFactory.selectFrom(organizationLicense)
				.where(
					organizationLicense.organizationId.eq(organizationId),
					organizationLicense.contractId.eq(contractId)
				)
				.fetchOne());
	}

	@Override
	public List<OrganizationLicense> getOrganizationLicenses(
		Long organizationId, OrganizationLicenseStatus status, ProductType productType
	) {
		return queryFactory
			.selectFrom(organizationLicense)
			.where(
				organizationLicense.organizationId.eq(organizationId),
				organizationLicense.status.eq(status),
				eqProductType(productType)
			).fetch();
	}

	@Override
	public Optional<OrganizationLicense> getOrganizationLicense(
		Long organizationId, OrganizationLicenseStatus organizationLicenseStatus, ProductType productType,
		LicenseGradeType licenseGradeType
	) {
		return getOrganizationLicense(
			organizationId, organizationLicenseStatus, null, productType, licenseGradeType);
	}

	@Override
	public Optional<OrganizationLicense> getOrganizationLicense(
		Long organizationId, OrganizationLicenseStatus organizationLicenseStatus, Boolean isExposed,
		ProductType productType, LicenseGradeType licenseGradeType
	) {
		return Optional.ofNullable(
			queryFactory.selectFrom(organizationLicense)
				.join(license).on(organizationLicense.licenseId.eq(license.id))
				.join(licenseGrade).on(license.licenseGradeId.eq(licenseGrade.id))
				.join(item).on(item.licenseId.eq(license.id))
				.where(
					organizationLicense.organizationId.eq(organizationId),
					organizationLicense.status.eq(organizationLicenseStatus),
					eqLicenseGradeType(licenseGradeType),
					eqIsExposed(isExposed),
					eqProductType(productType)
				)
				.fetchOne());
	}

	private BooleanExpression eqIsExposed(Boolean isExposed) {
		if (isExposed == null) {
			return null;
		}
		return item.isExposed.eq(isExposed);
	}

	@Override
	public Page<OrganizationLicenseResponseDto> getOrganizationLicenseResponses(
		OrganizationLicenseSearchDto organizationLicenseSearchDto, Pageable pageable
	) {
		QueryResults<OrganizationLicenseResponseDto> pagingResult = queryFactory
			.select(new QOrganizationLicenseResponseDto(
					organizationLicense.id
					, organizationLicense.organizationId
					, organizationLicense.contractId
					, organizationLicense.itemId
					, organizationLicense.productId
					, organizationLicense.productName
					, organizationLicense.productType
					, organizationLicense.licenseGradeId
					, organizationLicense.licenseGradeName
					, organizationLicense.licenseGradeType
					, organizationLicense.licenseId
					, organizationLicense.licenseName
					, organizationLicense.status
					, organizationLicense.startedAt
					, organizationLicense.expiredAt
					, organizationLicense.createdDate
					, organizationLicense.updatedDate
					, organizationLicense.createdBy
					, organizationLicense.lastModifiedBy
				)
			)
			.from(organizationLicense)
			.where(
				eqId(organizationLicenseSearchDto.getOrganizationLicenseId())
				, eqOrganizationId(organizationLicenseSearchDto.getOrganizationId())
				, eqContractId(organizationLicenseSearchDto.getContractId())
				, eqProductId(organizationLicenseSearchDto.getProductId())
				, eqLicenseGradeId(organizationLicenseSearchDto.getLicenseGradeId())
				, eqLicenseId(organizationLicenseSearchDto.getLicenseId())
				, eqStatus(organizationLicenseSearchDto.getStatus())
				, eqProductType(organizationLicenseSearchDto.getProductType())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(organizationLicense.id.desc())
			.fetchResults();

		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}

	@Override
	public Optional<OrganizationLicenseResponseDto> getOrganizationLicenseResponse(Long id) {
		return Optional.ofNullable(
			queryFactory.select(
					new QOrganizationLicenseResponseDto(
						organizationLicense.id
						, organizationLicense.organizationId
						, organizationLicense.contractId
						, organizationLicense.itemId
						, organizationLicense.productId
						, organizationLicense.productName
						, organizationLicense.productType
						, organizationLicense.licenseGradeId
						, organizationLicense.licenseGradeName
						, organizationLicense.licenseGradeType
						, organizationLicense.licenseGradeDescription
						, organizationLicense.licenseId
						, organizationLicense.licenseName
						, organizationLicense.licenseDescription
						, organizationLicense.licenseSalesTarget
						, organizationLicense.status
						, organizationLicense.startedAt
						, organizationLicense.expiredAt
						, organizationLicense.createdDate
						, organizationLicense.updatedDate
						, organizationLicense.createdBy
						, organizationLicense.lastModifiedBy
					)
				)
				.from(organizationLicense)
				.where(organizationLicense.id.eq(id))
				.fetchOne());
	}

}
