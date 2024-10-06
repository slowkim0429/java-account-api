package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QEmailCustomizingManagement.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementSearchDto;
import com.virnect.account.adapter.inbound.dto.response.EmailManagementResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QEmailManagementResponseDto;
import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.EmailCustomizingManagement;
import com.virnect.account.port.outbound.EmailManagementRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class EmailManagementRepositoryImpl implements EmailManagementRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqEmailType(Mail emailType) {
		if (emailType == null) {
			return null;
		}
		return emailCustomizingManagement.emailType.eq(emailType);
	}

	private BooleanExpression eqUseStatus(UseStatus useStatus) {
		if (useStatus == null) {
			return null;
		}
		return emailCustomizingManagement.useStatus.eq(useStatus);
	}

	@Override
	public Page<EmailManagementResponseDto> getEmailManagementResponses(
		EmailManagementSearchDto emailManagementSearchDto, Pageable pageable
	) {
		QueryResults<EmailManagementResponseDto> pageResult = queryFactory
			.select(new QEmailManagementResponseDto(
				emailCustomizingManagement.id,
				emailCustomizingManagement.emailType,
				emailCustomizingManagement.contentsInlineImageUrl,
				emailCustomizingManagement.description,
				emailCustomizingManagement.useStatus,
				emailCustomizingManagement.createdDate,
				emailCustomizingManagement.updatedDate,
				emailCustomizingManagement.createdBy,
				emailCustomizingManagement.lastModifiedBy
			))
			.from(emailCustomizingManagement)
			.where(
				eqEmailType(emailManagementSearchDto.getEmailType()),
				eqUseStatus(emailManagementSearchDto.getUseStatus())
			)
			.orderBy(emailCustomizingManagement.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(pageResult.getResults(), pageable, pageResult.getTotal());
	}

	@Override
	public Optional<EmailCustomizingManagement> getEmailCustomizingManagement(Long id) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(emailCustomizingManagement)
				.where(
					emailCustomizingManagement.id.eq(id)
				)
				.fetchOne()
		);
	}

	@Override
	public Optional<EmailCustomizingManagement> getEmailCustomizingManagement(Mail emailType, UseStatus useStatus) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(emailCustomizingManagement)
				.where(
					emailCustomizingManagement.emailType.eq(emailType),
					emailCustomizingManagement.useStatus.eq(useStatus)
				)
				.fetchFirst()
		);
	}

	@Override
	public Optional<EmailManagementResponseDto> getEmailCustomizingManagementResponse(Long emailManagementId) {
		return Optional.ofNullable(
			queryFactory
				.select(new QEmailManagementResponseDto(
					emailCustomizingManagement.id,
					emailCustomizingManagement.emailType,
					emailCustomizingManagement.contentsInlineImageUrl,
					emailCustomizingManagement.description,
					emailCustomizingManagement.useStatus,
					emailCustomizingManagement.createdDate,
					emailCustomizingManagement.updatedDate,
					emailCustomizingManagement.createdBy,
					emailCustomizingManagement.lastModifiedBy
				))
				.from(emailCustomizingManagement)
				.where(
					emailCustomizingManagement.id.eq(emailManagementId)
				).fetchOne()
		);
	}
}
