package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QUpdateGuide.*;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideSearchDto;
import com.virnect.account.adapter.inbound.dto.response.QUpdateGuideResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UpdateGuideResponseDto;
import com.virnect.account.domain.enumclass.ServiceType;
import com.virnect.account.domain.model.UpdateGuide;
import com.virnect.account.port.outbound.UpdateGuideRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class UpdateGuideRepositoryImpl implements UpdateGuideRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<UpdateGuide> getUpdateGuide(Long updateGuideId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(updateGuide)
				.where(updateGuide.id.eq(updateGuideId))
				.fetchOne()
		);
	}

	@Override
	public Optional<UpdateGuideResponseDto> getUpdateGuideResponse(Long updateGuideId) {
		return getUpdateGuideResponse(updateGuideId, null, null);
	}

	@Override
	public Page<UpdateGuideResponseDto> getUpdateGuides(UpdateGuideSearchDto updateGuideSearchDto, Pageable pageable) {
		QueryResults<UpdateGuideResponseDto> pagingResults = queryFactory.select(
				new QUpdateGuideResponseDto(
					updateGuide.id,
					updateGuide.name,
					updateGuide.serviceType,
					updateGuide.fileType,
					updateGuide.fileUrl,
					updateGuide.dateByUpdate,
					updateGuide.title,
					updateGuide.description,
					updateGuide.subTitle,
					updateGuide.subDescription,
					updateGuide.isExposed,
					updateGuide.createdBy,
					updateGuide.createdDate,
					updateGuide.lastModifiedBy,
					updateGuide.updatedDate
				)
			)
			.from(updateGuide)
			.where(
				eqId(updateGuideSearchDto.getId()),
				startWithName(updateGuideSearchDto.getName()),
				eqExposed(updateGuideSearchDto.getIsExposed())
			).orderBy(updateGuide.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();
		return new PageImpl<>(pagingResults.getResults(), pageable, pagingResults.getTotal());
	}

	@Override
	public Optional<UpdateGuideResponseDto> getUpdateGuideResponse(String serviceType, Boolean isExposed) {
		return getUpdateGuideResponse(null, serviceType, isExposed);
	}

	private Optional<UpdateGuideResponseDto> getUpdateGuideResponse(
		Long updateGuideId, String serviceType, Boolean isExposed
	) {
		return Optional.ofNullable(queryFactory.select(new QUpdateGuideResponseDto(
						updateGuide.id,
						updateGuide.name,
						updateGuide.serviceType,
						updateGuide.fileType,
						updateGuide.fileUrl,
						updateGuide.dateByUpdate,
						updateGuide.title,
						updateGuide.description,
						updateGuide.subTitle,
						updateGuide.subDescription,
						updateGuide.isExposed,
						updateGuide.createdBy,
						updateGuide.createdDate,
						updateGuide.lastModifiedBy,
						updateGuide.updatedDate
					)
				)
				.from(updateGuide)
				.where(
					eqId(updateGuideId),
					eqServiceType(serviceType),
					eqIsExposed(isExposed)
				)
				.orderBy(updateGuide.id.desc())
				.fetchFirst()
		);
	}

	private BooleanExpression startWithName(String name) {
		if (name == null) {
			return null;
		}
		return updateGuide.name.startsWith(name);
	}

	private BooleanExpression eqIsExposed(Boolean isExposed) {
		if (isExposed == null) {
			return null;
		}
		return updateGuide.isExposed.eq(isExposed);
	}

	private BooleanExpression eqId(Long id) {
		if (id == null) {
			return null;
		}
		return updateGuide.id.eq(id);
	}

	private BooleanExpression eqServiceType(String serviceType) {
		if (StringUtils.isBlank(serviceType)) {
			return null;
		}
		ServiceType requestedServiceType = ServiceType.valueOf(serviceType.toUpperCase());
		return updateGuide.serviceType.eq(requestedServiceType);
	}

	private BooleanExpression eqExposed(Boolean isExposed) {
		if (isExposed == null) {
			return null;
		}
		return updateGuide.isExposed.eq(isExposed);
	}
}
