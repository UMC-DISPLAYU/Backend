package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SchoolEmailVerificationPersistenceAdapter
    implements SchoolEmailVerificationRepository {

  private final SchoolEmailVerificationJpaRepository jpaRepository;

  @Override
  public SchoolEmailVerification save(SchoolEmailVerification verification) {
    return jpaRepository.save(verification);
  }

  @Override
  public Optional<SchoolEmailVerification> findByUserIdAndSchoolEmail(
      Long userId, String schoolEmail) {
    return jpaRepository.findTopByUser_IdAndSchoolEmailOrderByCreatedAtDesc(userId, schoolEmail);
  }

  @Override
  public void deleteByUserIdAndSchoolEmail(Long userId, String schoolEmail) {
    jpaRepository.deleteByUser_IdAndSchoolEmail(userId, schoolEmail);
  }
}
