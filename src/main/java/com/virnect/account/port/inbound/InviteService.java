package com.virnect.account.port.inbound;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.InviteUserAssignRequestDto;
import com.virnect.account.adapter.inbound.dto.request.invite.InviteRequestDto;
import com.virnect.account.adapter.inbound.dto.request.invite.InviteSearchDto;
import com.virnect.account.adapter.inbound.dto.request.invite.WorkspaceInviteRequestDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.invite.InviteResponseDto;
import com.virnect.account.adapter.inbound.dto.response.invite.InviteRevisionResponseDto;

public interface InviteService {
	void createInvite(InviteRequestDto inviteRequestDto);

	void assignUser(InviteUserAssignRequestDto inviteUserAssignRequestDto);

	PageContentResponseDto<InviteResponseDto> getInviteOfWorkspaceType(Long workspaceId, Pageable pageable);

	void cancelInvite(Long inviteId);

	void createWorkspaceInvite(WorkspaceInviteRequestDto workspaceInviteRequestDto);

	PageContentResponseDto<InviteResponseDto> getInvites(InviteSearchDto inviteSearchDto, Pageable pageable);

	InviteResponseDto getInvite(Long inviteId);

	List<InviteRevisionResponseDto> getInviteRevisions(Long inviteId);

}
