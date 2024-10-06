package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.AdminUserRole;

public interface AdminUserRoleRepository extends JpaRepository<AdminUserRole, Long>, AdminUserRoleRepositoryCustom {
}
