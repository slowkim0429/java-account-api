package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.account.domain.model.OrganizationLicense;

@Repository
public interface OrganizationLicenseRepository
	extends JpaRepository<OrganizationLicense, Long>, OrganizationLicenseRepositoryCustom {
}
