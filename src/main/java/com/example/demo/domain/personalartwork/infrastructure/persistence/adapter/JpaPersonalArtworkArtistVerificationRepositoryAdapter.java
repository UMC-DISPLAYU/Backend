package com.example.demo.domain.personalartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.domain.personalartwork.infrastructure.persistence.PersonalArtworkUserVerificationJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPersonalArtworkArtistVerificationRepositoryAdapter
    implements ArtistVerificationRepository {

  private final PersonalArtworkUserVerificationJpaRepository jpaRepository;

  public JpaPersonalArtworkArtistVerificationRepositoryAdapter(
      PersonalArtworkUserVerificationJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean isVerifiedArtist(Long userId) {
    return jpaRepository.existsByUserIdAndVerifiedTrue(userId);
  }
}
