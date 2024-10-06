package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QItem.*;
import static com.virnect.account.domain.model.QLicenseAttribute.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.AdditionalItemAndLicenseAttributeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QAdditionalItemAndLicenseAttributeResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.DependencyType;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.port.outbound.ItemWithLicenseAttributeRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class ItemWithLicenseAttributeRepositoryImpl implements ItemWithLicenseAttributeRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<AdditionalItemAndLicenseAttributeResponseDto> getItemAndLicenseAttributes(
		ApprovalStatus status, Long licenseId
	) {
		return queryFactory
			.select(new QAdditionalItemAndLicenseAttributeResponseDto(
					item.id,
					item.name,
					item.recurringInterval,
					item.amount,
					item.itemType,
					licenseAttribute.additionalAttributeType,
					licenseAttribute.dataType,
					licenseAttribute.dataValue
				)
			)
			.from(item)
			.join(licenseAttribute).on(licenseAttribute.licenseId.eq(item.licenseId))
			.where(
				item.status.eq(status),
				item.itemType.eq(ItemType.ATTRIBUTE),
				licenseAttribute.attributeDependencyType.eq(DependencyType.INDEPENDENCE),
				item.useStatus.ne(UseStatus.DELETE),
				licenseAttribute.status.ne(UseStatus.DELETE),
				item.licenseId.eq(licenseId)
			)
			.fetch();
	}
}
