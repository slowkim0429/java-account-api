package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.OrganizationLicenseKey;

public interface OrganizationLicenseKeyRepository
	extends JpaRepository<OrganizationLicenseKey, Long>, OrganizationLicenseKeyRepositoryCustom {
}
