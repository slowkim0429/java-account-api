package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.Domain;

public interface DomainRepository extends JpaRepository<Domain, Long>, DomainRepositoryCustom {
}