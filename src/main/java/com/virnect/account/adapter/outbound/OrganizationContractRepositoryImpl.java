package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QOrganizationContract.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.OrganizationContractResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QOrganizationContractResponseDto;
import com.virnect.account.domain.model.OrganizationContract;
import com.virnect.account.port.outbound.OrganizationContractRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class OrganizationContractRepositoryImpl implements OrganizationContractRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<OrganizationContractResponseDto> getOrganizationContractResponse(Long contractId) {
		return Optional.ofNullable(
			queryFactory.select(
					new QOrganizationContractResponseDto(
						organizationContract.id
						, organizationContract.organizationId
						, organizationContract.contractId
						, organizationContract.itemId
						, organizationContract.itemName
						, organizationContract.itemType
						, organizationContract.recurringInterval
						, organizationContract.paymentType
						, organizationContract.status
						, organizationContract.startDate
						, organizationContract.endDate
						, organizationContract.createdDate
						, organizationContract.updatedDate
						, organizationContract.createdBy
						, organizationContract.lastModifiedBy
						, organizationContract.couponId
					)
				)
				.from(organizationContract)
				.where(organizationContract.contractId.eq(contractId))
				.fetchOne());

	}

	@Override
	public Optional<OrganizationContract> getOrganizationContract(Long contractId) {
		return Optional.ofNullable(
			queryFactory.selectFrom(organizationContract)
				.where(
					organizationContract.contractId.eq(contractId)
				)
				.fetchOne());
	}

}
