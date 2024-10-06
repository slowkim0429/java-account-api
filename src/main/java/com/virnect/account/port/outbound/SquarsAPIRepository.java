package com.virnect.account.port.outbound;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.virnect.account.adapter.outbound.request.GroupUserByInviteRequestDto;
import com.virnect.account.adapter.outbound.request.OrganizationLicenseSendDto;
import com.virnect.account.adapter.outbound.request.UserSendDto;

@FeignClient(name = "squars-studio-api", url="${feign.squars-studio-api.url}", primary = false)
public interface SquarsAPIRepository {
	@PostMapping(value = "/api/squars/workspaces/groups/users/assign")
	void createGroupUser(
		@RequestBody GroupUserByInviteRequestDto groupUserByInviteRequestDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/squars/organizations/{organizationId}/licenses")
	ResponseEntity<Void> syncOrganizationLicense(
		@PathVariable("organizationId") Long organizationId,
		@RequestBody @Valid OrganizationLicenseSendDto organizationLicenseSendDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/squars/users/synchronize")
	ResponseEntity<Void> syncUser(
		@RequestBody UserSendDto userSendDto, @RequestHeader("Authorization") String authorizationHeaderValue
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/admin/users/synchronize")
	ResponseEntity<Void> syncUserByAdmin(
		@RequestBody @Valid UserSendDto userSendDto,
		@RequestHeader("Authorization") String authorizationToken
	);
}
