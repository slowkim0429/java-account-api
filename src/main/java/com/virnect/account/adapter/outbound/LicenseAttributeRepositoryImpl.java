package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QLicenseAttribute.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.LicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.LicenseAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QLicenseAdditionalAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QLicenseAttributeResponseDto;
import com.virnect.account.domain.enumclass.DependencyType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.LicenseAttribute;
import com.virnect.account.port.outbound.LicenseAttributeRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class LicenseAttributeRepositoryImpl implements LicenseAttributeRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqDependencyType(DependencyType dependencyType) {
		if (dependencyType == null) {
			return null;
		}
		return licenseAttribute.attributeDependencyType.eq(dependencyType);
	}

	private BooleanExpression eqStatus(UseStatus useStatus) {
		if (useStatus == null) {
			return null;
		}
		return licenseAttribute.status.eq(useStatus);
	}

	@Override
	public List<LicenseAttributeResponseDto> getLicenseAttributeResponse(Long licenseId) {
		return queryFactory.select(new QLicenseAttributeResponseDto(
					licenseAttribute.id,
					licenseAttribute.attributeType,
					licenseAttribute.dataType,
					licenseAttribute.dataValue,
					licenseAttribute.status
				)
			)
			.from(licenseAttribute)
			.where(
				licenseAttribute.status.ne(UseStatus.DELETE),
				licenseAttribute.licenseId.eq(licenseId),
				eqDependencyType(DependencyType.DEPENDENCE)
			)
			.fetch();
	}

	@Override
	public List<LicenseAdditionalAttributeResponseDto> getLicenseAdditionalAttributeResponse(
		Long licenseId
	) {
		return queryFactory.select(new QLicenseAdditionalAttributeResponseDto(
					licenseAttribute.id,
					licenseAttribute.additionalAttributeType,
					licenseAttribute.dataType,
					licenseAttribute.dataValue,
					licenseAttribute.status
				)
			)
			.from(licenseAttribute)
			.where(
				licenseAttribute.status.ne(UseStatus.DELETE),
				licenseAttribute.licenseId.eq(licenseId),
				eqDependencyType(DependencyType.INDEPENDENCE)
			)
			.fetch();
	}

	@Override
	public List<LicenseAttribute> getLicenseAttributes(Long licenseId, DependencyType dependencyType) {
		return this.getLicenseAttributes(licenseId, null, dependencyType);
	}

	@Override
	public Optional<LicenseAttribute> getLicenseAttributeById(Long licenseAttributeId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(
					licenseAttribute
				)
				.where(
					licenseAttribute.id.eq(licenseAttributeId),
					licenseAttribute.status.ne(UseStatus.DELETE)
				)
				.fetchOne());
	}

	@Override
	public List<LicenseAttribute> getLicenseAttributes(Long[] licenseIds, DependencyType dependencyType) {
		return queryFactory.selectFrom(licenseAttribute)
			.where(
				licenseAttribute.status.ne(UseStatus.DELETE),
				licenseAttribute.licenseId.in(licenseIds),
				licenseAttribute.attributeDependencyType.eq(dependencyType)
			)
			.fetch();
	}

	@Override
	public List<LicenseAttribute> getLicenseAttributes(
		Long licenseId, UseStatus useStatus, DependencyType dependencyType
	) {
		return queryFactory.selectFrom(licenseAttribute)
			.where(
				licenseAttribute.licenseId.eq(licenseId),
				eqStatus(useStatus),
				licenseAttribute.attributeDependencyType.eq(dependencyType)
			)
			.fetch();
	}
}
