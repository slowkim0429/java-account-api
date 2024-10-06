package com.virnect.account.port.outbound;

import java.util.List;

import com.virnect.account.adapter.inbound.dto.response.invite.InviteRevisionResponseDto;

public interface InviteRevisionRepository {

	List<InviteRevisionResponseDto> getInviteRevisions(Long inviteId);
}
