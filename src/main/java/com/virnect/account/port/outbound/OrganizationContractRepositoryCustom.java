package com.virnect.account.port.outbound;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.virnect.account.adapter.inbound.dto.response.OrganizationContractResponseDto;
import com.virnect.account.domain.model.OrganizationContract;

@Repository
public interface OrganizationContractRepositoryCustom {

	Optional<OrganizationContractResponseDto> getOrganizationContractResponse(Long contractId);

	Optional<OrganizationContract> getOrganizationContract(Long contractId);

}
