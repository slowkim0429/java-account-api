package com.virnect.account.port.inbound;

import java.util.concurrent.CompletableFuture;

import com.virnect.account.adapter.outbound.request.GroupUserByInviteRequestDto;
import com.virnect.account.adapter.outbound.request.OrganizationLicenseSendDto;
import com.virnect.account.adapter.outbound.request.UserSendDto;

public interface SquarsApiService {
	void createGroupUser(GroupUserByInviteRequestDto groupUserByInviteRequestDto, String authorizationToken);

	void syncOrganizationLicense(OrganizationLicenseSendDto organizationLicenseSendDto, String authorizationToken);

	CompletableFuture<Void> syncOrganizationLicenseCompletableFuture(
		OrganizationLicenseSendDto organizationLicenseSendDto, String authorizationHeaderValue
	);

	void syncOrganizationLicenseByAdmin(OrganizationLicenseSendDto organizationLicenseSendDto);

	void syncUser(UserSendDto userSendDto);

	CompletableFuture<Void> syncUserCompletableFuture(UserSendDto userSendDto);

	void syncUserByAdmin(UserSendDto userSendDto);

	void syncUserByNonUser(
		UserSendDto userSendDto, String authorizationToken
	);

}
