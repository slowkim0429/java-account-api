package com.virnect.account.port.outbound;

import java.util.List;

import com.virnect.account.domain.model.AdminUserRole;

public interface AdminUserRoleRepositoryCustom {
	List<AdminUserRole> getAdminUserRoles(Long adminUserId);
}
