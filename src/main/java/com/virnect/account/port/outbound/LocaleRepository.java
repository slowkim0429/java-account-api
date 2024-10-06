package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.account.domain.model.ServiceRegionLocaleMapping;

public interface LocaleRepository extends JpaRepository<ServiceRegionLocaleMapping, Long>, LocaleRepositoryCustom {
}
