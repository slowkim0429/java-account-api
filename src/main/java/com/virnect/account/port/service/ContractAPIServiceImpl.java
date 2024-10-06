package com.virnect.account.port.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequest;
import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequestContextHolder;
import com.virnect.account.adapter.outbound.request.CouponSendDto;
import com.virnect.account.adapter.outbound.request.ItemSendDto;
import com.virnect.account.adapter.outbound.request.OrganizationSendDto;
import com.virnect.account.port.inbound.ContractAPIService;
import com.virnect.account.port.outbound.ContractAPIRepository;

@Service
@RequiredArgsConstructor
public class ContractAPIServiceImpl implements ContractAPIService {
	private final ContractAPIRepository contractAPIRepository;
	private final TaskExecutor asyncFeignTaskExecutor;

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncOrganization(
		Long organizationId, OrganizationSendDto organizationSendDto
	) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		contractAPIRepository.syncOrganization(
			organizationId, organizationSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}

	@Override
	public CompletableFuture<Void> syncOrganizationCompletableFuture(
		Long organizationId, OrganizationSendDto organizationSendDto
	) {
		return CompletableFuture.runAsync(() -> {
				CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
				contractAPIRepository.syncOrganization(
					organizationId, organizationSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
			}
			, asyncFeignTaskExecutor);
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncOrganizationByNonUser(
		Long organizationId, OrganizationSendDto organizationSendDto, String authorizationHeaderValue
	) {
		contractAPIRepository.syncOrganization(organizationId, organizationSendDto, authorizationHeaderValue);
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncOrganizationByAdmin(
		Long organizationId, OrganizationSendDto organizationSendDto
	) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		contractAPIRepository.syncAdminOrganization(
			organizationId, organizationSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncItem(ItemSendDto itemSendDto) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		contractAPIRepository.syncItem(itemSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}

	@Override
	@Async("asyncFeignTaskExecutor")
	public void syncCoupon(CouponSendDto couponSendDto) {
		CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
		contractAPIRepository.syncCoupon(couponSendDto, customHttpServletRequest.getAuthorizationHeaderValue());
	}
}
