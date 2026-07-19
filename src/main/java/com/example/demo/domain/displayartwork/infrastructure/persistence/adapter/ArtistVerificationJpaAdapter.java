package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.domain.displayartwork.infrastructure.persistence.UserVerificationJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ArtistVerificationJpaAdapter implements ArtistVerificationRepository {

  private final UserVerificationJpaRepository jpaRepository;

  public ArtistVerificationJpaAdapter(UserVerificationJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean isVerifiedArtist(Long userId) {
    return jpaRepository.existsByUserIdAndVerifiedTrue(userId);
  }
}
