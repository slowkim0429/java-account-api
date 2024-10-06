package com.virnect.account.port.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequest;
import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequestContextHolder;
import com.virnect.account.adapter.outbound.request.GroupUserByInviteRequestDto;
import com.virnect.account.adapter.outbound.request.OrganizationLicenseSendDto;
import com.virnect.account.adapter.outbound.request.UserSendDto;
import com.virnect.account.port.inbound.SquarsApiService;
import com.virnect.account.port.outbound.SquarsAPIRepository;

@Service
@RequiredArgsConstructor
public class SquarsAPIServiceImpl implements SquarsApiService {
	private final SquarsAPIRepository squarsAPIRepository;
	private final TaskExecutor asyncFeignTaskExecutor;

	@Override
	public void createGroupUser(GroupUserByInviteRequestDto groupUserByInviteRequestDto, String authorizationToken) {
		squarsAPIRepository.createGroupUser(groupUserByInviteRequestDto, authorizationToken);
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncOrganizationLicense(
		OrganizationLicenseSendDto organizationLicenseSendDto, String authorizationToken
	) {
		squarsAPIRepository.syncOrganizationLicense(
			organizationLicenseSendDto.getOrganizationId(), organizationLicenseSendDto,
			authorizationToken
		);
	}

	@Override
	public CompletableFuture<Void> syncOrganizationLicenseCompletableFuture(
		OrganizationLicenseSendDto organizationLicenseSendDto, String authorizationHeaderValue
	) {
		return CompletableFuture.runAsync(() -> {
				squarsAPIRepository.syncOrganizationLicense(
					organizationLicenseSendDto.getOrganizationId(), organizationLicenseSendDto,
					authorizationHeaderValue
				);
			}, asyncFeignTaskExecutor
		);
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncOrganizationLicenseByAdmin(OrganizationLicenseSendDto organizationLicenseSendDto) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		squarsAPIRepository.syncOrganizationLicense(
			organizationLicenseSendDto.getOrganizationId(), organizationLicenseSendDto,
			customHttpServletRequest.getAuthorizationHeaderValue()
		);
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncUser(UserSendDto userSendDto) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		squarsAPIRepository.syncUser(userSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}

	@Override
	public CompletableFuture<Void> syncUserCompletableFuture(UserSendDto userSendDto) {
		return CompletableFuture.runAsync(() -> {
				CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
				squarsAPIRepository.syncUser(userSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
			}
			, asyncFeignTaskExecutor);
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncUserByAdmin(UserSendDto userSendDto) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		squarsAPIRepository.syncUserByAdmin(userSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncUserByNonUser(UserSendDto userSendDto, String authorizationToken) {
		squarsAPIRepository.syncUser(userSendDto, authorizationToken);
	}

}
