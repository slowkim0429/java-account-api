package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long>, AdminUserRepositoryCustom{
}
