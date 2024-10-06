package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.License;

public interface LicenseRepository
	extends JpaRepository<License, Long>, LicenseRepositoryCustom {
}
