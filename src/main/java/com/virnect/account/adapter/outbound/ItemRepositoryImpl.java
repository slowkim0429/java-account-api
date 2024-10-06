package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QItem.*;
import static com.virnect.account.domain.model.QLicense.*;
import static com.virnect.account.domain.model.QLicenseAttribute.*;
import static com.virnect.account.domain.model.QLicenseGrade.*;
import static com.virnect.account.domain.model.QProduct.*;

import java.util.List;
import java.util.Objects;
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

import com.virnect.account.adapter.inbound.dto.request.item.ItemSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ItemWithLicenseGradeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QItemAndLicenseResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QItemDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QItemWithLicenseGradeResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.Item;
import com.virnect.account.port.outbound.ItemRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression neDelete() {
		return item.useStatus.ne(UseStatus.DELETE);
	}

	private BooleanExpression eqStatus(String status) {
		if (status == null || status.equals("")) {
			return null;
		}
		return item.status.eq(ApprovalStatus.valueOf(status));
	}

	private BooleanExpression eqItemType(String itemType) {
		if (itemType == null || itemType.equals("")) {
			return null;
		}
		return item.itemType.eq(ItemType.valueOf(itemType));
	}

	private BooleanExpression eqItemId(Long itemId) {
		if (itemId == null || itemId <= 0) {
			return null;
		}
		return item.id.eq(itemId);
	}

	private BooleanExpression likeItemName(String itemName) {
		if (StringUtils.isBlank(itemName)) {
			return null;
		}
		return item.name.like(itemName + "%");
	}

	@Override
	public Optional<Item> getItemById(Long itemId) {
		return Optional.ofNullable(
			queryFactory.selectFrom(item)
				.where(
					item.id.eq(itemId)
					, neDelete()
				)
				.fetchOne());
	}

	@Override
	public Optional<Item> getItem(
		Long productId, ItemType itemType, LicenseGradeType licenseGradeType,
		RecurringIntervalType recurringIntervalType, Boolean isExposed
	) {
		return Optional.ofNullable(
			queryFactory.selectFrom(item)
				.join(license).on(license.id.eq(item.licenseId))
				.join(licenseGrade).on(licenseGrade.id.eq(license.licenseGradeId))
				.where(
					license.productId.eq(productId),
					item.isExposed.eq(isExposed),
					item.recurringInterval.eq(recurringIntervalType),
					item.itemType.eq(itemType),
					licenseGrade.gradeType.eq(licenseGradeType),
					item.useStatus.ne(UseStatus.DELETE)
				)
				.fetchFirst()
		);
	}

	@Override
	public Page<ItemAndLicenseResponseDto> getItemList(ItemSearchDto itemSearchDto, Pageable pageable) {
		QueryResults<ItemAndLicenseResponseDto> pageResult = queryFactory
			.select(new QItemAndLicenseResponseDto(
				item.id,
				item.licenseId,
				item.name,
				item.itemType,
				item.paymentType,
				item.recurringInterval,
				product.id,
				product.productType,
				product.name,
				licenseGrade.id,
				licenseGrade.name,
				licenseGrade.gradeType,
				item.amount,
				item.useStatus,
				item.status,
				item.isExposed,
				item.createdDate,
				item.createdBy,
				item.updatedDate,
				item.lastModifiedBy
			))
			.from(item)
			.join(license).on(license.id.eq(item.licenseId))
			.join(licenseGrade).on(licenseGrade.id.eq(license.licenseGradeId))
			.join(product).on(product.id.eq(license.productId))
			.where(
				neDelete(),
				eqItemType(itemSearchDto.getItemType()),
				eqStatus(itemSearchDto.getStatus()),
				eqLicenseGradeType(itemSearchDto.getLicenseGradeType()),
				eqIsExposed(itemSearchDto.getIsExposed()),
				eqItemId(itemSearchDto.getItemId()),
				eqProductType(itemSearchDto.valueOfProductType()),
				likeItemName(itemSearchDto.getItemName())
			)
			.orderBy(item.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(pageResult.getResults(), pageable, pageResult.getTotal());
	}

	private BooleanExpression eqLicenseGradeType(String licenseGradeType) {
		if (StringUtils.isBlank(licenseGradeType)) {
			return null;
		}

		return licenseGrade.gradeType.eq(LicenseGradeType.valueOf(licenseGradeType));
	}

	private BooleanExpression eqProductType(ProductType productType) {
		if (productType == null) {
			return null;
		}

		return product.productType.eq(productType);
	}

	private BooleanExpression eqIsExposed(Boolean isExposed) {
		if (isExposed == null) {
			return null;
		}

		return item.isExposed.eq(isExposed);
	}

	@Override
	public Optional<Item> getItem(Long licenseId, ItemType itemType) {
		return Optional.ofNullable(
			queryFactory.selectFrom(item)
				.where(
					item.licenseId.eq(licenseId),
					item.itemType.eq(itemType),
					item.status.eq(ApprovalStatus.APPROVED),
					item.useStatus.ne(UseStatus.DELETE)
				)
				.fetchOne());
	}

	@Override
	public Optional<Item> getItem(
		LicenseGradeType licenseGradeType, ItemType itemType, Boolean isExposed, ProductType productType
	) {
		return Optional.ofNullable(
			queryFactory.selectFrom(item)
				.join(license).on(license.id.eq(item.licenseId))
				.join(licenseGrade).on(licenseGrade.id.eq(license.licenseGradeId))
				.join(product).on(product.id.eq(license.productId))
				.where(
					item.itemType.eq(itemType),
					item.status.eq(ApprovalStatus.APPROVED),
					item.useStatus.ne(UseStatus.DELETE),
					product.productType.eq(productType),
					licenseGrade.gradeType.eq(licenseGradeType),
					eqIsExposed(isExposed)
				)
				.fetchOne());
	}

	@Override
	public List<ItemWithLicenseGradeResponseDto> getItemResponses(
		ApprovalStatus itemApprovalStatus, boolean isExposed, RecurringIntervalType recurringIntervalType,
		ItemType itemType, ProductType productType
	) {
		return queryFactory.select(new QItemWithLicenseGradeResponseDto(
				item.id,
				item.name,
				item.recurringInterval,
				item.amount,
				item.monthlyUsedAmount,
				item.currencyType,
				item.itemType,
				item.paymentType,
				item.licenseId,
				license.description,
				license.salesTarget,
				licenseGrade.gradeType,
				licenseGrade.name,
				licenseGrade.description
			))
			.from(item)
			.join(license).on(license.id.eq(item.licenseId))
			.join(product).on(product.id.eq(license.productId))
			.join(licenseGrade).on(licenseGrade.id.eq(license.licenseGradeId))
			.where(
				item.isExposed.eq(isExposed),
				item.status.eq(itemApprovalStatus),
				item.useStatus.ne(UseStatus.DELETE),
				eqRecurringIntervalType(recurringIntervalType),
				item.itemType.eq(itemType),
				license.useStatus.ne(UseStatus.DELETE),
				product.productType.eq(productType)
			)
			.limit(100)
			.fetch();
	}

	public BooleanExpression eqRecurringIntervalType(RecurringIntervalType recurringIntervalType) {
		if (Objects.isNull(recurringIntervalType)) {
			return null;
		}
		return item.recurringInterval.eq(recurringIntervalType);
	}

	@Override
	public Optional<Item> getItem(
		Long licenseId, LicenseAdditionalAttributeType additionalAttributeType
	) {
		return Optional.ofNullable(
			queryFactory.selectFrom(item)
				.join(licenseAttribute).on(licenseAttribute.id.eq(item.licenseAttributeId))
				.where(
					item.licenseId.eq(licenseId),
					licenseAttribute.additionalAttributeType.eq(additionalAttributeType),
					item.status.eq(ApprovalStatus.APPROVED),
					item.useStatus.ne(UseStatus.DELETE)
				)
				.fetchFirst());
	}

	@Override
	public Optional<ItemDetailResponseDto> getItemDetailResponseDtoById(Long itemId) {
		return Optional.ofNullable(
			queryFactory.select(new QItemDetailResponseDto(
						item.id,
						item.name,
						product.id,
						product.productType,
						product.name,
						item.itemType,
						item.licenseId,
						license.licenseGradeId,
						licenseGrade.gradeType,
						licenseGrade.name,
						item.recurringInterval,
						item.amount,
						item.monthlyUsedAmount,
						item.licenseAttributeId,
						item.status,
						item.isExposed,
						item.createdDate.stringValue(),
						item.createdBy,
						item.updatedDate.stringValue(),
						item.lastModifiedBy,
						item.paymentType,
						item.currencyType,
						item.useStatus,
						item.hubSpotProductId
					)
				)
				.from(item)
				.join(license).on(license.id.eq(item.licenseId))
				.join(licenseGrade).on(licenseGrade.id.eq(license.licenseGradeId))
				.join(product).on(product.id.eq(license.productId))
				.where(
					item.id.eq(itemId),
					item.useStatus.ne(UseStatus.DELETE)
				)
				.fetchOne()
		);
	}
}
