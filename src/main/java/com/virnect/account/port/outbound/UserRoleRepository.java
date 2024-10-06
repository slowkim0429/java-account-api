package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.account.domain.model.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>, UserRoleRepositoryCustom {
}
