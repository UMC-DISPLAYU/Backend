package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.util.Optional;

public interface PersonalArtworkExistenceRepository {
  boolean existsById(Long personalArtworkId);

  boolean existsByIdAndUserId(Long personalArtworkId, Long userId);

  Optional<Long> findOwnerUserIdById(Long personalArtworkId);
}
