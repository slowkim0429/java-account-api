package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.MobileManagement;

public interface MobileManagementRepository
	extends JpaRepository<MobileManagement, Long>, MobileManagementRepositoryCustom {
}
