package com.virnect.account.port.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequest;
import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequestContextHolder;
import com.virnect.account.adapter.outbound.request.OrganizationLicenseSendDto;
import com.virnect.account.adapter.outbound.request.UserSendDto;
import com.virnect.account.adapter.outbound.request.WorkspaceUserCreateSendDto;
import com.virnect.account.port.inbound.WorkspaceAPIService;
import com.virnect.account.port.outbound.WorkspaceAPIRepository;

@Service
@RequiredArgsConstructor
public class WorkspaceAPIServiceImpl implements WorkspaceAPIService {
	private final WorkspaceAPIRepository workspaceAPIRepository;
	private final TaskExecutor asyncFeignTaskExecutor;

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncUser(UserSendDto userSendDto) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		workspaceAPIRepository.syncUser(userSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}

	@Override
	public CompletableFuture<Void> syncUserCompletableFuture(
		UserSendDto userSendDto
	) {
		return CompletableFuture.runAsync(() -> {
				CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
				workspaceAPIRepository.syncUser(userSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
			}
			, asyncFeignTaskExecutor);
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncUserByNonUser(
		UserSendDto userSendDto, String authorizationToken
	) {
		workspaceAPIRepository.syncUser(userSendDto, authorizationToken);
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncUserByAdmin(UserSendDto userSendDto) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		workspaceAPIRepository.syncUserByAdmin(userSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncOrganizationLicense(
		OrganizationLicenseSendDto organizationLicenseRequestDto, String authorizationToken
	) {
		workspaceAPIRepository.syncOrganizationLicense(
			organizationLicenseRequestDto, authorizationToken);

	}

	@Override
	public CompletableFuture<?> syncOrganizationLicenseCompletableFuture(
		OrganizationLicenseSendDto organizationLicenseSendDto, String authorizationHeaderValue
	) {
		return CompletableFuture.runAsync(
			() -> {
				workspaceAPIRepository.syncOrganizationLicense(
					organizationLicenseSendDto, authorizationHeaderValue);
			}, asyncFeignTaskExecutor
		);
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncOrganizationLicenseByAdmin(
		OrganizationLicenseSendDto organizationLicenseRequestDto
	) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		workspaceAPIRepository.syncOrganizationLicenseByAdmin(
			organizationLicenseRequestDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}

	@Override
	public void createWorkspaceUser(WorkspaceUserCreateSendDto requestDto, String authorizationToken) {
		workspaceAPIRepository.createWorkspaceUser(requestDto, authorizationToken);
	}

	@Override
	public void createWorkspaceUserByInvite(
		WorkspaceUserCreateSendDto workspaceUserCreateSendDto, String authorizationToken
	) {
		workspaceAPIRepository.createWorkspaceUserByInvite(workspaceUserCreateSendDto, authorizationToken);
	}

}
