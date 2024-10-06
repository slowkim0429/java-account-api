package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.Invite;

public interface InviteRepository extends JpaRepository<Invite, Long>, InviteRepositoryCustom {
}
