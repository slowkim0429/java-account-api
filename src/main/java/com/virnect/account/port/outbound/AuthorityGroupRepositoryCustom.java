package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupSearchDto;
import com.virnect.account.adapter.inbound.dto.response.authoritygroup.AuthorityGroupResponseDto;
import com.virnect.account.domain.model.AuthorityGroup;

public interface AuthorityGroupRepositoryCustom {
	Page<AuthorityGroupResponseDto> getAuthorityGroupResponses(
		AuthorityGroupSearchDto authorityGroupSearchDto, Pageable pageable
	);

	Optional<AuthorityGroupResponseDto> getAuthorityGroupResponse(Long authorityGroupId);

	Optional<AuthorityGroup> getAuthorityGroup(Long id);
}
