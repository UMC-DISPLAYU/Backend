package com.example.demo.domain.personalartworkcommunication.domain.repository;

public interface PersonalArtworkExistenceRepository {
  boolean existsById(Long personalArtworkId);

  boolean existsByIdAndUserId(Long personalArtworkId, Long userId);
}
