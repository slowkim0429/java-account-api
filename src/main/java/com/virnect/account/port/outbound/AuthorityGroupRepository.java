package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.AuthorityGroup;

public interface AuthorityGroupRepository extends JpaRepository<AuthorityGroup, Long>, AuthorityGroupRepositoryCustom {
}
