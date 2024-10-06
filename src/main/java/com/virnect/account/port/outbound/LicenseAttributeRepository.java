package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.LicenseAttribute;

public interface LicenseAttributeRepository
	extends JpaRepository<LicenseAttribute, Long>, LicenseAttributeRepositoryCustom {
}
