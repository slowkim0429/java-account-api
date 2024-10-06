package com.virnect.account.port.outbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.account.domain.model.UpdateGuide;

@Repository
public interface UpdateGuideRepository extends JpaRepository<UpdateGuide, Long>, UpdateGuideRepositoryCustom {
}
