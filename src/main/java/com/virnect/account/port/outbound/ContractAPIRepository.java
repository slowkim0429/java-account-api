package com.virnect.account.port.outbound;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.virnect.account.adapter.outbound.request.CouponSendDto;
import com.virnect.account.adapter.outbound.request.ItemSendDto;
import com.virnect.account.adapter.outbound.request.OrganizationSendDto;

@FeignClient(name = "contract-api", url="${feign.contract-api.url}", primary = false)
public interface ContractAPIRepository {
	@RequestMapping(method = RequestMethod.POST, value = "/api/admin/items/synchronize")
	ResponseEntity<Void> syncItem(
		@RequestBody ItemSendDto itemSendDto, @RequestHeader("Authorization") String authorizationToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/organizations/{organizationId}")
	ResponseEntity<Void> syncOrganization(
		@PathVariable("organizationId") Long organizationId,
		@RequestBody OrganizationSendDto organizationSendDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/organizations/{organizationId}")
	ResponseEntity<Void> syncOrganization(
		@PathVariable("organizationId") Long organizationId,
		@RequestBody OrganizationSendDto organizationSendDto
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/admin/organizations/{organizationId}")
	ResponseEntity<Void> syncAdminOrganization(
		@PathVariable("organizationId") Long organizationId,
		@RequestBody OrganizationSendDto organizationSendDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/admin/coupons/synchronize")
	ResponseEntity<Void> syncCoupon(
		@RequestBody CouponSendDto couponSendDto,
		@RequestHeader("Authorization") String authorizationToken
	);
}
