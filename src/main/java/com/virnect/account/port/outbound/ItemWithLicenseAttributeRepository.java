package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.Item;

public interface ItemWithLicenseAttributeRepository
	extends JpaRepository<Item, Long>, ItemWithLicenseAttributeRepositoryCustom {
}
