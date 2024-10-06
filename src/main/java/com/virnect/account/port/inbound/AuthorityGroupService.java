package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupModifyRequestDto;
import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.authoritygroup.AuthorityGroupResponseDto;
import com.virnect.account.adapter.inbound.dto.response.authoritygroup.AuthorityGroupRevisionResponseDto;
import com.virnect.account.domain.enumclass.UseStatus;

public interface AuthorityGroupService {
	PageContentResponseDto<AuthorityGroupResponseDto> getAuthorityGroups(
		AuthorityGroupSearchDto authorityGroupSearchDto, Pageable pageable
	);

	AuthorityGroupResponseDto getAuthorityGroupResponse(Long authorityGroupId);

	void create(AuthorityGroupCreateRequestDto authorityGroupCreateRequestDto);

	void validUseAuthorityGroup(Long authorityGroupId);

	void modify(Long authorityGroupId, AuthorityGroupModifyRequestDto authorityGroupModifyRequestDto);

	PageContentResponseDto<AuthorityGroupRevisionResponseDto> getRevisions(Long authorityGroupId, Pageable pageable);

	void updateStatus(Long authorityGroupId, UseStatus status);
}
