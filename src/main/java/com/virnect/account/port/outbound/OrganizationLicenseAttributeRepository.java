package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.account.domain.model.OrganizationLicenseAttribute;

@Repository
public interface OrganizationLicenseAttributeRepository
	extends JpaRepository<OrganizationLicenseAttribute, Long>, OrganizationLicenseAttributeRepositoryCustom {
}
