package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.aggregate.University;
import com.example.demo.domain.user.domain.repository.UniversityRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UniversityPersistenceAdapter implements UniversityRepository {

  private final UniversityJpaRepository universityJpaRepository;
  private final UniversityEmailDomainJpaRepository emailDomainJpaRepository;

  @Override
  public List<University> searchByName(String keyword) {
    return universityJpaRepository.searchByName(keyword);
  }

  @Override
  public Optional<University> findByName(String name) {
    return universityJpaRepository.findByNameOrAlias(name);
  }

  @Override
  public boolean existsEmailDomain(Long universityId, String domain) {
    return emailDomainJpaRepository.existsByUniversity_IdAndDomain(universityId, domain);
  }
}
