package com.virnect.account.port.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequest;
import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequestContextHolder;
import com.virnect.account.adapter.outbound.request.UserSendDto;
import com.virnect.account.port.inbound.NotificationAPIService;
import com.virnect.account.port.outbound.NotificationAPIRepository;

@Service
@RequiredArgsConstructor
public class NotificationAPIServiceImpl implements NotificationAPIService {
	private final NotificationAPIRepository notificationAPIRepository;
	private final TaskExecutor asyncFeignTaskExecutor;

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncUser(UserSendDto userSendDto) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		notificationAPIRepository.syncUser(userSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}

	@Override
	public CompletableFuture<Void> syncUserCompletableFuture(UserSendDto userSendDto) {
		return CompletableFuture.runAsync(() -> {
				CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
				notificationAPIRepository.syncUser(
					userSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
			}
			, asyncFeignTaskExecutor);
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncUserByNonUser(
		UserSendDto userSendDto, String authorizationToken
	) {
		notificationAPIRepository.syncUser(userSendDto, authorizationToken);
	}

	@Override
	public void syncUserByAdmin(UserSendDto userSendDto) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		notificationAPIRepository.syncUserByAdmin(userSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}
}
