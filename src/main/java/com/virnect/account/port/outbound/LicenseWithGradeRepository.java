package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.account.domain.model.License;

@Repository
public interface LicenseWithGradeRepository extends JpaRepository<License, Long>, LicenseWithGradeRepositoryCustom {

}
