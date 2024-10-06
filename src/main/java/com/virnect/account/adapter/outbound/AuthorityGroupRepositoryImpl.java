package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QAuthorityGroup.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupSearchDto;
import com.virnect.account.adapter.inbound.dto.response.authoritygroup.AuthorityGroupResponseDto;
import com.virnect.account.adapter.inbound.dto.response.authoritygroup.QAuthorityGroupResponseDto;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.AuthorityGroup;
import com.virnect.account.port.outbound.AuthorityGroupRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class AuthorityGroupRepositoryImpl implements AuthorityGroupRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<AuthorityGroupResponseDto> getAuthorityGroupResponses(
		AuthorityGroupSearchDto authorityGroupSearchDto, Pageable pageable
	) {
		QueryResults<AuthorityGroupResponseDto> pageResult = queryFactory
			.select(new QAuthorityGroupResponseDto(
				authorityGroup.id,
				authorityGroup.name,
				authorityGroup.description,
				authorityGroup.status,
				authorityGroup.createdBy,
				authorityGroup.createdDate,
				authorityGroup.lastModifiedBy,
				authorityGroup.updatedDate
			))
			.from(authorityGroup)
			.where(eqStatus(authorityGroupSearchDto.statusValueOf()))
			.orderBy(authorityGroup.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();
		return new PageImpl<>(pageResult.getResults(), pageable, pageResult.getTotal());
	}

	@Override
	public Optional<AuthorityGroupResponseDto> getAuthorityGroupResponse(Long authorityGroupId) {
		return Optional.ofNullable(
			queryFactory
				.select(new QAuthorityGroupResponseDto(
					authorityGroup.id,
					authorityGroup.name,
					authorityGroup.description,
					authorityGroup.status,
					authorityGroup.createdBy,
					authorityGroup.createdDate,
					authorityGroup.lastModifiedBy,
					authorityGroup.updatedDate
				))
				.from(authorityGroup)
				.where(authorityGroup.id.eq(authorityGroupId))
				.fetchOne()
		);
	}

	@Override
	public Optional<AuthorityGroup> getAuthorityGroup(Long id) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(authorityGroup)
				.where(authorityGroup.id.eq(id))
				.fetchOne()
		);
	}

	private BooleanExpression eqStatus(UseStatus status) {
		if (status == null) {
			return null;
		}
		return authorityGroup.status.eq(status);
	}
}
