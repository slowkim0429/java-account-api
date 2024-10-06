package com.virnect.account.port.outbound;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.response.authoritygroup.AuthorityGroupRevisionResponseDto;

public interface AuthorityGroupRevisionRepository {
	Page<AuthorityGroupRevisionResponseDto> getAuthorityGroupRevisionResponses(
		Long authorityGroupId, Pageable pageable
	);
}
