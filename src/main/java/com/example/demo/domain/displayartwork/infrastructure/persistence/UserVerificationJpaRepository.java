package com.example.demo.domain.displayartwork.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVerificationJpaRepository
    extends JpaRepository<UserVerificationJpaEntity, Long> {

  boolean existsByUserIdAndVerifiedTrue(Long userId);
}
