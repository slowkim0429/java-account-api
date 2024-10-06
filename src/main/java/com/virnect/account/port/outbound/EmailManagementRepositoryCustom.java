package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementSearchDto;
import com.virnect.account.adapter.inbound.dto.response.EmailManagementResponseDto;
import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.EmailCustomizingManagement;

public interface EmailManagementRepositoryCustom {

	Page<EmailManagementResponseDto> getEmailManagementResponses(
		EmailManagementSearchDto emailManagementSearchDto, Pageable pageable
	);

	Optional<EmailCustomizingManagement> getEmailCustomizingManagement(Long id);

	Optional<EmailCustomizingManagement> getEmailCustomizingManagement(Mail emailType, UseStatus useStatus);

	Optional<EmailManagementResponseDto> getEmailCustomizingManagementResponse(Long emailManagementId);
}