package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.ExternalServiceMapping;

public interface ExternalServiceMappingRepository
	extends JpaRepository<ExternalServiceMapping, Long>, ExternalServiceMappingRepositoryCustom {
}
