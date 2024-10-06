package com.virnect.account.port.inbound;

import java.util.concurrent.CompletableFuture;

import com.virnect.account.adapter.outbound.request.CouponSendDto;
import com.virnect.account.adapter.outbound.request.ItemSendDto;
import com.virnect.account.adapter.outbound.request.OrganizationSendDto;

public interface ContractAPIService {
	void syncOrganization(
		Long organizationId, OrganizationSendDto organizationSendDto
	);

	CompletableFuture<Void> syncOrganizationCompletableFuture(
		Long organizationId, OrganizationSendDto organizationSendDto
	);

	void syncOrganizationByNonUser(
		Long organizationId, OrganizationSendDto organizationSendDto,
		String authorizationHeaderValue
	);

	void syncOrganizationByAdmin(
		Long organizationId, OrganizationSendDto organizationSendDto
	);

	void syncItem(ItemSendDto itemSendDto);

	void syncCoupon(CouponSendDto couponSendDto);
}
