package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.account.domain.model.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>, OrganizationRepositoryCustom {
}
