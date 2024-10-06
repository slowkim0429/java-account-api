package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.account.domain.model.User;

@Repository
public interface AuthRepository extends JpaRepository<User, Long>, AuthRepositoryCustom {
}
