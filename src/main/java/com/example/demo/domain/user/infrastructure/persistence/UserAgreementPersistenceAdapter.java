package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.UserAgreement;
import com.example.demo.domain.user.domain.repository.UserAgreementRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAgreementPersistenceAdapter implements UserAgreementRepository {

  private final UserAgreementJpaRepository userAgreementJpaRepository;

  @Override
  public List<UserAgreement> saveAll(List<UserAgreement> userAgreements) {
    return userAgreementJpaRepository.saveAll(userAgreements);
  }
}
