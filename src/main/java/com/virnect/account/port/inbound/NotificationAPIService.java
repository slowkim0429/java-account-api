package com.virnect.account.port.inbound;

import java.util.concurrent.CompletableFuture;

import com.virnect.account.adapter.outbound.request.UserSendDto;

public interface NotificationAPIService {
	void syncUser(UserSendDto userSendDto);

	void syncUserByNonUser(
		UserSendDto userSendDto, String authorizationToken
	);

	void syncUserByAdmin(UserSendDto userSendDto);

	CompletableFuture<Void> syncUserCompletableFuture(UserSendDto userSendDto);
}
