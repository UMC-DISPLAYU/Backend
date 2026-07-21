package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalArtworkExistenceJpaRepository
    extends JpaRepository<PersonalArtworkReferenceJpaEntity, Long> {

  boolean existsByPersonalArtworkIdAndUserId(Long personalArtworkId, Long userId);
}
