package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.invite.InviteSearchDto;
import com.virnect.account.adapter.inbound.dto.response.invite.InviteResponseDto;
import com.virnect.account.domain.enumclass.InviteStatus;
import com.virnect.account.domain.enumclass.InviteType;
import com.virnect.account.domain.model.Invite;

public interface InviteRepositoryCustom {
	Optional<Invite> getInvite(
		Long workspaceId, Long groupId, String receiverEmail, InviteStatus inviteStatus, InviteType inviteType
	);

	Optional<Invite> getInvite(Long id);

	Optional<InviteResponseDto> getInviteResponse(Long id);

	Optional<Invite> getWorkspaceInvite(Long workspaceInviteId);

	Page<InviteResponseDto> getInviteResponses(
		Long organizationId, Long workspaceId, InviteType inviteType, Pageable pageable
	);

	Page<InviteResponseDto> getInviteResponses(
		InviteSearchDto inviteSearchDto, Pageable pageable
	);
}
