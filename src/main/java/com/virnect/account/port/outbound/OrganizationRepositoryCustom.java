package com.virnect.account.port.outbound;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationSearchDto;
import com.virnect.account.domain.model.Organization;

public interface OrganizationRepositoryCustom {
	Optional<Organization> getOrganization(Long organizationId);

	Page<Organization> getOrganizations(
		OrganizationSearchDto organizationSearchDto, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable
	);
}
