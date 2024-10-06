package com.virnect.account.port.inbound;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationContractRequestDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationContractResponseDto;

public interface OrganizationContractService {
	void sync(OrganizationContractRequestDto organizationContractRequestDto);

	OrganizationContractResponseDto getOrganizationContractByAdmin(Long contractId);

	OrganizationContractResponseDto getOrganizationContract(Long contractId);
}
