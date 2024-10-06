package com.virnect.account.port.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.mobilemanagement.MobileManagementUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.MobileManagementNoticeResponseDto;
import com.virnect.account.adapter.inbound.dto.response.MobileManagementResponseDto;
import com.virnect.account.domain.model.MobileManagement;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.ControlModeAccessHistoryService;
import com.virnect.account.port.inbound.MobileManagementService;
import com.virnect.account.port.outbound.MobileManagementRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class MobileManagementServiceImpl implements MobileManagementService {

	private final PasswordEncoder passwordEncoder;
	private final ControlModeAccessHistoryService controlModeAccessHistoryService;
	private final MobileManagementRepository mobileManagementRepository;

	@Transactional(readOnly = true)
	@Override
	public void passwordVerification(String requestPassword) {
		MobileManagement mobileManagement = getMobileManagement();
		boolean matches = passwordEncoder.matches(requestPassword, mobileManagement.getPassword());
		if (!matches) {
			controlModeAccessHistoryService.accessFailed();
			throw new CustomException(ErrorCode.INVALID_MOBILE_MANAGEMENT_NOT_MATCHED_PASSWORD);
		}
		controlModeAccessHistoryService.accessSucceeded();
	}

	@Transactional(readOnly = true)
	@Override
	public MobileManagementNoticeResponseDto getExposedNotice() {
		return mobileManagementRepository.getMobileManagementNoticeResponse(true).orElse(null);
	}

	@Transactional(readOnly = true)
	@Override
	public MobileManagementResponseDto getMobileManagementResponse() {
		return mobileManagementRepository.getMobileManagementResponse()
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MOBILE_MANAGEMENT));
	}

	@Transactional(readOnly = true)
	@Override
	public MobileManagement getMobileManagement() {
		return mobileManagementRepository.getMobileManagement()
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MOBILE_MANAGEMENT));
	}

	@Override
	public void update(MobileManagementUpdateRequestDto mobileManagementUpdateRequestDto) {
		MobileManagement mobileManagement = getMobileManagement();
		mobileManagement.updateNotice(mobileManagementUpdateRequestDto);
		if (mobileManagementUpdateRequestDto.isExistNewPassword()) {
			mobileManagement.setPassword(passwordEncoder.encode(mobileManagementUpdateRequestDto.getNewPassword()));
		}
	}
}
