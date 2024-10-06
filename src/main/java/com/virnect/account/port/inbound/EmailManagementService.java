package com.virnect.account.port.inbound;

import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementRequestDto;
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementSearchDto;
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementUseStatusUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.EmailManagementResponseDto;
import com.virnect.account.adapter.inbound.dto.response.EmailManagementRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.model.EmailCustomizingManagement;

public interface EmailManagementService {

	PageContentResponseDto<EmailManagementResponseDto> getEmailManagements(
		EmailManagementSearchDto emailManagementSearchDto, Pageable pageable
	);

	void createEmailManagement(EmailManagementRequestDto emailManagementRequestDto);

	void updateUseStatusOfEmailManagement(
		Long emailManagementId, EmailManagementUseStatusUpdateRequestDto emailManagementUseStatusUpdateRequestDto
	);

	Optional<EmailCustomizingManagement> getUsingEmailManagement(Mail emailType);

	PageContentResponseDto<EmailManagementRevisionResponseDto> getEmailManagementRevisions(
		Long emailManagementId, Pageable pageable
	);

	void updateEmailManagement(Long emailManagementId, EmailManagementUpdateRequestDto emailManagementUpdateRequestDto);

	EmailManagementResponseDto getEmailManagement(Long emailManagementId);
}
