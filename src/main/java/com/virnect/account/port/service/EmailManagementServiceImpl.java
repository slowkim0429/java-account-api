package com.virnect.account.port.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementRequestDto;
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementSearchDto;
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementUseStatusUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.EmailManagementResponseDto;
import com.virnect.account.adapter.inbound.dto.response.EmailManagementRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.FileDirectory;
import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.EmailCustomizingManagement;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.EmailManagementService;
import com.virnect.account.port.inbound.FileService;
import com.virnect.account.port.outbound.EmailManagementRepository;
import com.virnect.account.port.outbound.EmailManagementRevisionRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailManagementServiceImpl implements EmailManagementService {
	private final EmailManagementRepository emailManagementRepository;
	private final EmailManagementRevisionRepository emailManagementRevisionRepository;
	private final FileService fileService;

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<EmailManagementResponseDto> getEmailManagements(
		EmailManagementSearchDto emailManagementSearchDto, Pageable pageable
	) {
		Page<EmailManagementResponseDto> emailManagementResponses = emailManagementRepository.getEmailManagementResponses(
			emailManagementSearchDto, pageable);
		return new PageContentResponseDto<>(emailManagementResponses, pageable);
	}

	@Override
	public void createEmailManagement(EmailManagementRequestDto emailManagementRequestDto) {
		String uploadedFileUrl = fileService.upload(
			emailManagementRequestDto.getContentsInlineImage(), FileDirectory.EMAIL_CUSTOMIZING_MANAGEMENT);

		EmailCustomizingManagement newEmailCustomizingManagement = EmailCustomizingManagement.of(
			emailManagementRequestDto, uploadedFileUrl);
		emailManagementRepository.save(newEmailCustomizingManagement);
	}

	@Override
	public void updateUseStatusOfEmailManagement(
		Long emailManagementId, EmailManagementUseStatusUpdateRequestDto useStatusUpdateRequestDto
	) {
		EmailCustomizingManagement emailCustomizingManagement = emailManagementRepository.getEmailCustomizingManagement(
			emailManagementId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EMAIL_CUSTOMIZING_MANAGEMENT));

		if (useStatusUpdateRequestDto.getUseStatus().isUse()) {
			getUsingEmailManagement(emailCustomizingManagement.getEmailType()).ifPresent(
				EmailCustomizingManagement::unuse);
		}
		emailCustomizingManagement.setUseStatus(useStatusUpdateRequestDto.getUseStatus());
	}

	@Override
	public Optional<EmailCustomizingManagement> getUsingEmailManagement(Mail emailType) {
		return emailManagementRepository.getEmailCustomizingManagement(emailType, UseStatus.USE);
	}

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<EmailManagementRevisionResponseDto> getEmailManagementRevisions(
		Long emailManagementId, Pageable pageable
	) {
		Page<EmailManagementRevisionResponseDto> emailManagementRevisions = emailManagementRevisionRepository.getEmailManagementRevisions(
			emailManagementId, pageable);
		return new PageContentResponseDto<>(emailManagementRevisions, pageable);
	}

	@Override
	public void updateEmailManagement(
		Long emailManagementId, EmailManagementUpdateRequestDto emailManagementUpdateRequestDto
	) {
		EmailCustomizingManagement emailCustomizingManagement = emailManagementRepository.getEmailCustomizingManagement(
			emailManagementId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EMAIL_CUSTOMIZING_MANAGEMENT));

		if (emailCustomizingManagement.getUseStatus().isUse()) {
			throw new CustomException(ErrorCode.INVALID_STATUS);
		}

		if (emailManagementUpdateRequestDto.getEmailType() != null) {
			emailCustomizingManagement.setEmailType(emailManagementUpdateRequestDto.getEmailType());
		}

		if (!emailManagementUpdateRequestDto.isContentsInlineImageEmpty()) {
			String uploadedFileUrl = fileService.replaceFileUrl(
				emailCustomizingManagement.getContentsInlineImageUrl(),
				emailManagementUpdateRequestDto.getContentsInlineImage(), FileDirectory.EMAIL_CUSTOMIZING_MANAGEMENT
			);
			emailCustomizingManagement.setContentsInlineImageUrl(uploadedFileUrl);
		}

		if (StringUtils.isNotBlank(emailManagementUpdateRequestDto.getDescription())) {
			emailCustomizingManagement.setDescription(emailManagementUpdateRequestDto.getDescription());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public EmailManagementResponseDto getEmailManagement(Long emailManagementId) {
		return emailManagementRepository.getEmailCustomizingManagementResponse(emailManagementId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EMAIL_CUSTOMIZING_MANAGEMENT));
	}
}
