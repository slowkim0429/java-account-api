package com.virnect.account.port.outbound;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.UserRevisionResponseDto;

@Repository
public interface UserRevisionRepository {
	List<UserRevisionResponseDto> getUserRevisionResponses(Long userId);
}
