package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.LicenseGrade;

public interface LicenseGradeRepository extends JpaRepository<LicenseGrade, Long>, LicenseGradeRepositoryCustom {
}
