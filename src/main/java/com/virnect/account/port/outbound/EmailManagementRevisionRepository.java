package com.virnect.account.port.outbound;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.response.EmailManagementRevisionResponseDto;

public interface EmailManagementRevisionRepository {
	Page<EmailManagementRevisionResponseDto> getEmailManagementRevisions(Long emailManagementId, Pageable pageable);
}
