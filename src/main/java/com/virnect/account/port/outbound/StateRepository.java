package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.ServiceLocaleState;

public interface StateRepository extends JpaRepository<ServiceLocaleState, Long>, StateRepositoryCustom {
}
