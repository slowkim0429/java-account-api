package com.virnect.account.port.inbound;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationSearchDto;
import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationResponseDto;
import com.virnect.account.adapter.inbound.dto.response.OrganizationRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.UserResponseDto;
import com.virnect.account.domain.model.Organization;
import com.virnect.account.domain.model.ServiceRegionLocaleMapping;
import com.virnect.account.domain.model.User;
import com.virnect.account.security.service.CustomUserDetails;

public interface OrganizationService {
	Organization create(User user, ServiceRegionLocaleMapping locale);

	void update(Long organizationId, OrganizationUpdateRequestDto organizationUpdateRequestDto);

	UserResponseDto getOrganizationUser(Long organizationId);

	OrganizationResponseDto getOrganizationById(Long organizationId);

	OrganizationResponseDto getCurrentOrganization(CustomUserDetails customUserDetails);

	PageContentResponseDto<OrganizationResponseDto> getOrganizations(
		OrganizationSearchDto organizationSearchDto, Pageable pageable
	);

	void synchronizeOrganizationByAdmin(Long organizationId);

	List<OrganizationRevisionResponseDto> getOrganizationRevision(Long organizationId);
}
