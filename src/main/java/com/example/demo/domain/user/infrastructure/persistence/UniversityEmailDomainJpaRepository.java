package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.UniversityEmailDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityEmailDomainJpaRepository
    extends JpaRepository<UniversityEmailDomain, Long> {

  boolean existsByUniversity_IdAndDomain(Long universityId, String domain);
}
