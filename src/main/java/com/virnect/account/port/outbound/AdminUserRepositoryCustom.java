package com.virnect.account.port.outbound;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.user.AdminUserSearchDto;
import com.virnect.account.adapter.inbound.dto.response.AdminUserResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.model.AdminUser;

public interface AdminUserRepositoryCustom {
	Page<AdminUserResponseDto> getAdminUserResponses(AdminUserSearchDto adminUserSearchDto, Pageable pageable);

	Optional<AdminUser> getAdminUser(String email);

	Optional<AdminUser> getAdminUser(Long adminUserId);

	Optional<AdminUser> getAdminUser(MembershipStatus status, ApprovalStatus approvalStatus, Long authorityGroupId);

	List<AdminUser> getAdminUsersByRole(Role role);

	Optional<AdminUserResponseDto> getAdminUserResponse(Long userId);
}
