package com.virnect.account.port.outbound;

import com.virnect.account.domain.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long>, ErrorLogRepositoryCustom {
}
