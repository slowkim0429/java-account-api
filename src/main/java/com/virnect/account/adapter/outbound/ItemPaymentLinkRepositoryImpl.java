package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QItemPaymentLink.*;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.item.ItemPaymentLinkSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ItemPaymentLinkResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QItemPaymentLinkResponseDto;
import com.virnect.account.domain.model.ItemPaymentLink;
import com.virnect.account.port.outbound.ItemPaymentLinkRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class ItemPaymentLinkRepositoryImpl implements ItemPaymentLinkRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression eqItemId(Long itemId) {
		if (itemId == null || itemId <= 0) {
			return null;
		}
		return itemPaymentLink.itemId.eq(itemId);
	}

	private BooleanExpression eqUserId(Long userId) {
		if (userId == null || userId <= 0) {
			return null;
		}
		return itemPaymentLink.userId.eq(userId);
	}

	private BooleanExpression startsWithEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return null;
		}
		return itemPaymentLink.email.startsWith(email);
	}

	private BooleanExpression startsWithEmailDomain(String emailDomain) {
		if (StringUtils.isBlank(emailDomain)) {
			return null;
		}
		return itemPaymentLink.emailDomain.startsWith(emailDomain);
	}

	@Override
	public Optional<ItemPaymentLink> getItemPaymentLink(Long userId, Long itemId) {
		return Optional.ofNullable(
			queryFactory
				.select(itemPaymentLink)
				.where(
					itemPaymentLink.userId.eq(userId),
					itemPaymentLink.itemId.eq(itemId)
				)
				.from(itemPaymentLink)
				.orderBy(itemPaymentLink.id.desc())
				.fetchFirst());
	}

	@Override
	public Page<ItemPaymentLinkResponseDto> getItemPaymentLinkResponses(
		ItemPaymentLinkSearchDto itemPaymentLinkSearchDto, Pageable pageable
	) {
		QueryResults<ItemPaymentLinkResponseDto> pageResult = queryFactory
			.select(new QItemPaymentLinkResponseDto(
				itemPaymentLink.id,
				itemPaymentLink.itemId,
				itemPaymentLink.userId,
				itemPaymentLink.email,
				itemPaymentLink.emailDomain,
				itemPaymentLink.expiredDate,
				itemPaymentLink.lastModifiedBy,
				itemPaymentLink.updatedDate,
				itemPaymentLink.createdBy,
				itemPaymentLink.createdDate
			))
			.from(itemPaymentLink)
			.where(
				eqItemId(itemPaymentLinkSearchDto.getItemId()),
				eqUserId(itemPaymentLinkSearchDto.getUserId()),
				startsWithEmail(itemPaymentLinkSearchDto.getEmail()),
				startsWithEmailDomain(itemPaymentLinkSearchDto.getEmailDomain())
			)
			.orderBy(itemPaymentLink.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(pageResult.getResults(), pageable, pageResult.getTotal());
	}

}

