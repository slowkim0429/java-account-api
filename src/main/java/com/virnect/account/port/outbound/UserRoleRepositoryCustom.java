package com.virnect.account.port.outbound;

import java.util.List;
import java.util.Optional;

import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.model.UserRole;

public interface UserRoleRepositoryCustom {
	List<UserRole> getUserUseRoles(Long userId);

	Optional<UserRole> getUserRoleByUserIdAndRole(Long userId, Role role);
}
