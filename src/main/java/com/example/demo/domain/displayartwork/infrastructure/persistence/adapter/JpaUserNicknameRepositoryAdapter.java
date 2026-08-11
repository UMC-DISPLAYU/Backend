package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.domain.repository.UserNicknameRepository;
import com.example.demo.domain.displayartwork.infrastructure.persistence.UserVerificationJpaEntity;
import com.example.demo.domain.displayartwork.infrastructure.persistence.UserVerificationJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaUserNicknameRepositoryAdapter implements UserNicknameRepository {

  private final UserVerificationJpaRepository jpaRepository;

  public JpaUserNicknameRepositoryAdapter(UserVerificationJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<String> findNicknameById(Long userId) {
    return jpaRepository.findById(userId).map(UserVerificationJpaEntity::getNickname);
  }
}
