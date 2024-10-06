package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QItem.*;
import static com.virnect.account.domain.model.QLicense.*;
import static com.virnect.account.domain.model.QLicenseGrade.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.ItemAndLicenseGradeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QItemAndLicenseGradeResponseDto;
import com.virnect.account.port.outbound.ItemWithLicenseGradeRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class ItemWithLicenseGradeRepositoryImpl implements ItemWithLicenseGradeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<ItemAndLicenseGradeResponseDto> getItemAndLicenseResponse(Long itemId) {
		return Optional.ofNullable(
			queryFactory.select(new QItemAndLicenseGradeResponseDto(
					item.id,
					item.name,
					item.recurringInterval,
					item.amount,
					item.itemType,
					item.licenseId,
					item.monthlyUsedAmount,
					licenseGrade.gradeType,
					licenseGrade.name,
					license.salesTarget
				))
				.from(item)
				.join(license).on(license.id.eq(item.licenseId))
				.join(licenseGrade).on(licenseGrade.id.eq(license.licenseGradeId))
				.where(
					item.id.eq(itemId)
				)
				.fetchOne()
		);
	}
}
