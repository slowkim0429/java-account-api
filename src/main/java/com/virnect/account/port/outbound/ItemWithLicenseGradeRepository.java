package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.Item;

public interface ItemWithLicenseGradeRepository
	extends JpaRepository<Item, Long>, ItemWithLicenseGradeRepositoryCustom {
}
