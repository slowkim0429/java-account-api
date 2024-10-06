package com.virnect.account.port.outbound;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.OrganizationRevisionResponseDto;

@Repository
public interface OrganizationRevisionRepository {
	List<OrganizationRevisionResponseDto> getOrganizationRevisionResponses(Long organizationId);
}
