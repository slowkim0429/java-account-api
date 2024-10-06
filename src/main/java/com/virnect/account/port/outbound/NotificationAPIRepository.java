package com.virnect.account.port.outbound;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.virnect.account.adapter.outbound.request.UserSendDto;

@FeignClient(name = "notification-api", url = "${feign.notification-api.url}", primary = false)
public interface NotificationAPIRepository {
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
}
