package com.virnect.account.port.inbound;

import java.util.concurrent.CompletableFuture;

import com.virnect.account.adapter.outbound.request.OrganizationLicenseSendDto;
import com.virnect.account.adapter.outbound.request.UserSendDto;
import com.virnect.account.adapter.outbound.request.WorkspaceUserCreateSendDto;

public interface WorkspaceAPIService {
	void syncUser(UserSendDto userSendDto);

	CompletableFuture<Void> syncUserCompletableFuture(
		UserSendDto userSendDto
	);

	void syncUserByNonUser(
		UserSendDto userSendDto, String authorizationToken
	);

	void syncUserByAdmin(UserSendDto userSendDto);

	void syncOrganizationLicense(
		OrganizationLicenseSendDto organizationLicenseRequestDto, String authorizationToken
	);

	CompletableFuture<?> syncOrganizationLicenseCompletableFuture(
		OrganizationLicenseSendDto organizationLicenseSendDto, String authorizationHeaderValue
	);

	void syncOrganizationLicenseByAdmin(
		OrganizationLicenseSendDto organizationLicenseRequestDto
	);

	void createWorkspaceUser(WorkspaceUserCreateSendDto requestDto, String authorizationToken);

	void createWorkspaceUserByInvite(WorkspaceUserCreateSendDto workspaceUserCreateSendDto, String authorizationToken);

}
