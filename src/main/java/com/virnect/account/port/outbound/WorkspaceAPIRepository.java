package com.virnect.account.port.outbound;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.virnect.account.adapter.outbound.request.OrganizationLicenseSendDto;
import com.virnect.account.adapter.outbound.request.UserSendDto;
import com.virnect.account.adapter.outbound.request.WorkspaceUserCreateSendDto;

@FeignClient(name = "workspace-api", url="${feign.workspace-api.url}", primary = false)
public interface WorkspaceAPIRepository {
	@RequestMapping(method = RequestMethod.POST, value = "/api/users/synchronize")
	ResponseEntity<Void> syncUser(
		@RequestBody @Valid UserSendDto userSendDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/admin/users/synchronize")
	ResponseEntity<Void> syncUserByAdmin(
		@RequestBody @Valid UserSendDto userSendDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/organizations/licenses")
	ResponseEntity<Void> syncOrganizationLicense(
		@RequestBody @Valid OrganizationLicenseSendDto organizationLicenseRequestDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/admin/organizations/licenses")
	ResponseEntity<Void> syncOrganizationLicenseByAdmin(
		@RequestBody @Valid OrganizationLicenseSendDto organizationLicenseRequestDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/workspaces/users")
	ResponseEntity<Void> createWorkspaceUser(
		@RequestBody WorkspaceUserCreateSendDto requestDto,
		@RequestHeader("Authorization") String authorizationToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/api/workspaces/invited-group-users")
	ResponseEntity<Void> createWorkspaceUserByInvite(
		@RequestBody WorkspaceUserCreateSendDto workspaceUserCreateSendDto,
		@RequestHeader("Authorization") String authorizationToken
	);
}
