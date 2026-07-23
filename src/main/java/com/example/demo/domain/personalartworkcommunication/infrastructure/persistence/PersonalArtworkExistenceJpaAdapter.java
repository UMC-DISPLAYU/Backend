package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersonalArtworkExistenceJpaAdapter implements PersonalArtworkExistenceRepository {

  private final PersonalArtworkExistenceJpaRepository repository;

  @Override
  public boolean existsById(Long personalArtworkId) {
    return repository.existsByPersonalArtworkIdAndDeletedAtIsNull(personalArtworkId);
  }

  @Override
  public boolean existsByIdAndUserId(Long personalArtworkId, Long userId) {
    return repository.existsByPersonalArtworkIdAndUserIdAndDeletedAtIsNull(
        personalArtworkId, userId);
  }

  @Override
  public Optional<Long> findOwnerUserIdById(Long personalArtworkId) {
    return repository.findOwnerUserIdById(personalArtworkId);
  }
}
