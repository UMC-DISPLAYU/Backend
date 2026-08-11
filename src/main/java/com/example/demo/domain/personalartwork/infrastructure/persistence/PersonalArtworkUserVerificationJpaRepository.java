package com.example.demo.domain.personalartwork.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalArtworkUserVerificationJpaRepository
    extends JpaRepository<PersonalArtworkUserVerificationJpaEntity, Long> {

  boolean existsByUserIdAndVerifiedTrue(Long userId);
}
