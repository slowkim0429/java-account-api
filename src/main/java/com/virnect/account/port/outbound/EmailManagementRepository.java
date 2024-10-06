package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.EmailCustomizingManagement;

public interface EmailManagementRepository extends JpaRepository<EmailCustomizingManagement, Long>, EmailManagementRepositoryCustom {
}